package com.payment_gateway.razorpay.payment.dto.request;

import com.payment_gateway.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentInitRequest(
        UUID orderId,
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails
) {
}
