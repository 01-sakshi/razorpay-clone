package com.payment_gateway.razorpay.payment.dto.request;

import com.payment_gateway.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

/**
 * Request to authorize a payment attempt for an existing order.
 *
 * @param orderId order being attempted
 * @param paymentMethod selected payment method
 * @param methodDetails method-specific data; may be empty or {@code null} according to the selected gateway
 */
public record PaymentInitRequest(
        UUID orderId,
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails
) {
}
