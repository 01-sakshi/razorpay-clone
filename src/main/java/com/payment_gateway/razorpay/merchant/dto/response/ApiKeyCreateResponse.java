package com.payment_gateway.razorpay.merchant.dto.response;

import com.payment_gateway.razorpay.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
