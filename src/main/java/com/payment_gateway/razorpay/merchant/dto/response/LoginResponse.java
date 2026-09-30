package com.payment_gateway.razorpay.merchant.dto.response;

/**
 * Successful authentication response.
 *
 * @param accessToken signed bearer token for authenticated requests
 */
public record LoginResponse(
        String accessToken
) {
}
