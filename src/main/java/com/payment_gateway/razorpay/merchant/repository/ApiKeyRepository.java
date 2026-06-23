package com.payment_gateway.razorpay.merchant.repository;

import com.payment_gateway.razorpay.merchant.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    List<ApiKey> findByMerchantId(UUID merchantId);
    Optional<ApiKey> findApiKeyByKeyIdAndMerchantId(String keyId, UUID merchantId);
}
