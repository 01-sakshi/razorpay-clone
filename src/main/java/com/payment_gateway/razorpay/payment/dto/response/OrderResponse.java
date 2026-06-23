package com.payment_gateway.razorpay.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.OrderStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        UUID orderId,
        UUID merchantId,
        Map<String, Object> notes,
        String receipt,     //This is order-id at merchant's end
        Money amount,
        OrderStatus status,
        Integer attempts,
        Instant expiresAt,
        Instant createdAt
) {
}
