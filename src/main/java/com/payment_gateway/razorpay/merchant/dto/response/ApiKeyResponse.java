package com.payment_gateway.razorpay.merchant.dto.response;

import com.payment_gateway.razorpay.common.enums.Environment;

import java.time.Instant;
import java.util.UUID;

public record ApiKeyResponse(
        UUID id,
        String keyId,
        Environment environment,
        boolean enabled,
        Instant lastUsedAt,
        Instant createdAt

) {
}
