package com.payment_gateway.razorpay.merchant.dto.response;

import com.payment_gateway.razorpay.common.enums.BusinessType;
import com.payment_gateway.razorpay.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
}
