package com.payment_gateway.razorpay.merchant.dto.request;

import com.payment_gateway.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
