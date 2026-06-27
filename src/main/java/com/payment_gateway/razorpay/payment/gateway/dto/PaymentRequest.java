package com.payment_gateway.razorpay.payment.gateway.dto;

import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentRequest(
        UUID paymentId,
        UUID merchantId,
        UUID orderId,
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails,
        Money amount
) {
}
