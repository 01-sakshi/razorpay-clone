package com.payment_gateway.razorpay.vault.dto.response;

import com.payment_gateway.razorpay.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        Integer expiryMonth,
        Integer expiryYear,
        CardBrand cardBrand,
        String lastFour
) {
}
