package com.payment_gateway.razorpay.merchant.serviceImpl;

import com.payment_gateway.razorpay.common.constants.Constants;
import com.payment_gateway.razorpay.common.exceptions.ApiKeyDisabledException;
import com.payment_gateway.razorpay.common.exceptions.ResourceNotFoundException;
import com.payment_gateway.razorpay.common.util.RandomizerUtil;
import com.payment_gateway.razorpay.merchant.cache.ApiKeyCache;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
    private final ApiKeyCache apiKeyCache;

    /**
     * Verifies the merchant exists, generates environment-prefixed credentials, persists only the BCrypt secret hash,
     * and returns the raw secret in the response once.
     *
     * @throws ResourceNotFoundException if the merchant does not exist
     */
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
                .keySecretHash(BCRYPT.encode(keySecret))
                .environment(createApiKeyRequest.environment())
                .build();
        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponse(apiKey.getId(), apiKey.getKeyId(), keySecret, apiKey.getEnvironment());
    }

    /** Maps all keys for the merchant to public metadata; secret hashes are not included in the response DTO. */
    @Override
    public List<ApiKeyResponse> list(UUID merchantId) {
        return apiKeyMapper.toApikeyResponseList(apiKeyRepository.findByMerchantId(merchantId));
    }

    /**
     * Disables the key addressed by both merchant ID and key ID and evicts its Redis entry; repeat revocation is a no-op.
     *
     * @throws ResourceNotFoundException if the key is absent or belongs to another merchant
     */
    @Override
    @Transactional
    public String revoke(UUID merchantId, String keyId) {
        Optional<ApiKey> optionalApiKey = apiKeyRepository.findApiKeyByKeyIdAndMerchantId(keyId, merchantId);
        if (optionalApiKey.isEmpty()) throw new ResourceNotFoundException("apiKey", keyId);

        ApiKey apiKey = optionalApiKey.get();
        if (apiKey.getEnabled()) apiKey.setEnabled(false);
        else return "Api Key already revoked";

        //Remove entry from cache as well
        apiKeyCache.evict(keyId);

        /* Dirty Checking: Since @Transactional is added, it will automatically detect the change in entity
            and persist it, no need to explicitly call save() */
//        apiKeyRepository.save(apiKey);
        return "Api Key revoked";
    }

    /**
     * Rotates a merchant-owned enabled key by retaining its current hash, setting a one-hour grace expiry, and storing
     * a new BCrypt hash; evicts cached metadata before returning replacement credentials.
     *
     * @throws ResourceNotFoundException if the key is absent or belongs to another merchant
     * @throws ApiKeyDisabledException if the key has already been revoked
     */
    @Override
    @Transactional
    public ApiKeyCreateResponse rotate(UUID merchantId, String keyId) {
        Optional<ApiKey> optionalApiKey = apiKeyRepository.findApiKeyByKeyIdAndMerchantId(keyId, merchantId);
        if (optionalApiKey.isEmpty()) throw new ResourceNotFoundException("apiKey", keyId);

        ApiKey apiKey = optionalApiKey.get();
        if (!apiKey.getEnabled())
            throw new ApiKeyDisabledException("API_KEY_DISABLED", "APIKEY", keyId, "Cannot rotate a disabled API Key");
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setGracePeriodExpiresAt(Instant.now().plusSeconds(60 * 60));
        apiKey.setKeySecretHash(BCRYPT.encode(RandomizerUtil.randomBase64(40)));
        apiKey.setUpdatedAt(Instant.now());
        apiKey.setUpdatedBy(Constants.SYSTEM);

        apiKeyCache.evict(keyId);   //Remove entry from cache as well
        apiKey = apiKeyRepository.save(apiKey);
        return apiKeyMapper.toApiKeyCreateResponse(apiKey);
    }
}
