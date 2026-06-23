package com.payment_gateway.razorpay.merchant.serviceImpl;

import com.payment_gateway.razorpay.common.constants.Constants;
import com.payment_gateway.razorpay.common.exceptions.ApiKeyDisabledException;
import com.payment_gateway.razorpay.common.exceptions.ResourceNotFoundException;
import com.payment_gateway.razorpay.common.util.RandomizerUtil;
import com.payment_gateway.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyResponse;
import com.payment_gateway.razorpay.merchant.entity.ApiKey;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import com.payment_gateway.razorpay.merchant.mapper.ApiKeyMapper;
import com.payment_gateway.razorpay.merchant.repository.ApiKeyRepository;
import com.payment_gateway.razorpay.merchant.repository.MerchantRepository;
import com.payment_gateway.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;
    private final ApiKeyMapper apiKeyMapper;

    @Override
    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest) {

        Merchant merchant = merchantRepository.findById(merchantId).
                orElseThrow(() -> new ResourceNotFoundException("merchant", merchantId));

        String keyId = "rzp_" + createApiKeyRequest.environment().name().toLowerCase() + "_" + RandomizerUtil.randomBase64(24);
        String keySecret = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(keySecret)   //TODO: Hash the key secret to persist
                .environment(createApiKeyRequest.environment())
                .build();
        return apiKeyMapper.toApiKeyCreateResponse(apiKeyRepository.save(apiKey));
    }

    @Override
    public List<ApiKeyResponse> list(UUID merchantId) {
        return apiKeyMapper.toApikeyResponseList(apiKeyRepository.findByMerchantId(merchantId));
    }

    @Override
    @Transactional
    public String revoke(UUID merchantId, String keyId) {
        Optional<ApiKey> optionalApiKey = apiKeyRepository.findApiKeyByKeyIdAndMerchantId(keyId, merchantId);
        if(optionalApiKey.isEmpty()) throw new ResourceNotFoundException("apiKey", keyId);

        ApiKey apiKey = optionalApiKey.get();
        if(apiKey.getEnabled()) apiKey.setEnabled(false);
        else return "Api Key already revoked";
        /*Since @Transactional is added, it will automatically detect the change in entity
        and persist it, no need to explicitly call save()*/
        apiKeyRepository.save(apiKey);
        return "Api Key revoked";
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, String keyId) {
        Optional<ApiKey> optionalApiKey = apiKeyRepository.findApiKeyByKeyIdAndMerchantId(keyId, merchantId);
        if(optionalApiKey.isEmpty()) throw new ResourceNotFoundException("apiKey", keyId);

        ApiKey apiKey = optionalApiKey.get();
        if(!apiKey.getEnabled()) throw new ApiKeyDisabledException("API_KEY_DISABLED", "APIKEY", keyId, "Cannot rotate a disabled API Key");
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setGracePeriodExpiresAt(Instant.now().plusSeconds(60*60));
        apiKey.setKeySecretHash(RandomizerUtil.randomBase64(40));
        apiKey.setUpdatedAt(Instant.now());
        apiKey.setUpdatedBy(Constants.SYSTEM);
        apiKey = apiKeyRepository.save(apiKey);
        return apiKeyMapper.toApiKeyCreateResponse(apiKey);
    }
}
