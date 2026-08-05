package com.payment_gateway.razorpay.merchant.cache;

import com.payment_gateway.razorpay.common.enums.Environment;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyCacheEntry(
        UUID merchantId,
        String keyId,
        String keySecretHash,
        String previousKeySecretHash,
        Environment environment,
        Boolean enabled,
        Instant gracePeriodExpiresAt

) {

    public Boolean isInGracePeriod() {
        return gracePeriodExpiresAt != null && Instant.now().isBefore(gracePeriodExpiresAt);
    }
}
