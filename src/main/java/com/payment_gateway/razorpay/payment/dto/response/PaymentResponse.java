package com.payment_gateway.razorpay.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.PaymentMethod;
import com.payment_gateway.razorpay.common.enums.PaymentStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponse(
        UUID id,
        UUID merchantId,
        UUID orderId,
        PaymentStatus status,
        Money amount,
        PaymentMethod method,
        Map<String, Object> methodDetails,
        String bankReference,
        Instant authorizedAt,
        Instant capturedAt,
        Instant failedAt,
        Instant refundedAt,
        Instant settledAt,
        String errorCode,
        String errorDescription
) {
}
