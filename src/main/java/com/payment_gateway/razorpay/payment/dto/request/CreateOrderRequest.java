package com.payment_gateway.razorpay.payment.dto.request;

import com.payment_gateway.razorpay.common.entity.Money;

import java.time.Instant;
import java.util.Map;

public record CreateOrderRequest(
        Map<String, Object> notes,
        Instant expiresAt,
        String receipt,     //This is order-id at merchant's end
        Money amount
) {
}
