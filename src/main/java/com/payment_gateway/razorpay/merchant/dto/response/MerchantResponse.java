package com.payment_gateway.razorpay.merchant.dto.response;

import com.payment_gateway.razorpay.common.enums.BusinessType;
import com.payment_gateway.razorpay.common.enums.MerchantStatus;

import java.util.UUID;

/**
 * Public merchant profile returned by registration and lookup operations.
 *
 * @param id merchant identifier
 * @param name merchant owner or display name
 * @param email merchant account email
 * @param businessName registered business name
 * @param businessType business classification, or {@code null} when omitted
 * @param merchantStatus current merchant lifecycle status
 */
public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
}
