package com.payment_gateway.razorpay.merchant.dto.response;

import com.payment_gateway.razorpay.common.enums.Environment;

import java.util.UUID;

/**
 * One-time API-key creation response.
 *
 * @param id credential identifier
 * @param keyId public key identifier
 * @param keySecret raw secret returned only during creation or rotation; never persist or log it
 * @param environment credential environment
 */
public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
