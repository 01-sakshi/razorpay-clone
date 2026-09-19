package com.payment_gateway.razorpay.merchant.api;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;

import com.payment_gateway.razorpay.common.dto.SettlementBankDetails;
import com.payment_gateway.razorpay.common.dto.WebhookTarget;
import com.payment_gateway.razorpay.common.enums.MerchantStatus;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import com.payment_gateway.razorpay.merchant.repository.MerchantRepository;
import com.payment_gateway.razorpay.merchant.repository.WebhookConfigRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantLookupServiceImpl implements MerchantLookupService {

    private final WebhookConfigRepository webhookConfigRepository;
    private final MerchantRepository merchantRepository;
    private final BytesEncryptor bytesEncryptor;

    @Override
    public List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType) {

        return webhookConfigRepository.findByMerchantIdAndEnabledTrue(merchantId).stream()
                .filter((config) -> config.isSubscribedTo(eventType))
                .map((config) -> {
                    byte[] cipherBytes = Base64.getDecoder().decode(config.getWebhookSecret());
                    byte[] decryptedSecretBytes = bytesEncryptor.decrypt(cipherBytes);
                    return new WebhookTarget(config.getId(), config.getTargetUrl(),
                            new String(decryptedSecretBytes, StandardCharsets.UTF_8));
                })
                .toList();
    }

    @Override
    public List<UUID> getAllActiveMerchants() {
        List<UUID> merchantIds = merchantRepository.findAllIdsByStatus(MerchantStatus.ACTIVE);
        log.info("All active merchant ids: {}", merchantIds);
        return merchantIds;
    }

    @Override
    public SettlementBankDetails getSettlementBankDetails(UUID merchantId) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("No merchant found for merchant id: " + merchantId));
        return new SettlementBankDetails(merchant.getSettlementBankAccount(), merchant.getSettlementBankIfsc(),
                merchant.getSettlementBankAccountHolderName());
    }

}
