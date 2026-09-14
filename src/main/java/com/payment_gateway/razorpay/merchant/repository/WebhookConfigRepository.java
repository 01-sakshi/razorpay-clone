package com.payment_gateway.razorpay.merchant.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.payment_gateway.razorpay.merchant.entity.MerchantWebhookConfig;

public interface WebhookConfigRepository extends JpaRepository<MerchantWebhookConfig, UUID> {

    List<MerchantWebhookConfig> findByMerchantId(UUID merchantId);

    Optional<MerchantWebhookConfig> findByMerchantIdAndId(UUID merchantId, UUID webhookConfigId);

    void deleteByMerchantIdAndId(UUID merchantId, UUID webhookConfigId);

    List<MerchantWebhookConfig> findByMerchantIdAndEnabledTrue(UUID merchantId);
    
}
