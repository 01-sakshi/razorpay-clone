package com.payment_gateway.razorpay.merchant.dto.request;

import com.payment_gateway.razorpay.common.enums.Environment;

/**
 * Request to create an API key for one environment.
 *
 * @param environment credential environment; must be a supported {@link com.payment_gateway.razorpay.common.enums.Environment}
 */
public record CreateApiKeyRequest(
        Environment environment
) {
}
