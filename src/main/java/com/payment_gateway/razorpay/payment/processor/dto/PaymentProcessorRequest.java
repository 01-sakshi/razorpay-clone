package com.payment_gateway.razorpay.payment.processor.dto;

import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID processingId,
        UUID paymentId,
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails,
        String pan,
        String expiry,      //Doubt: expiryMonth or expiryYear or both?
        Money amount
) {

    public static PaymentProcessorRequest card(UUID paymentId, String pan, String expiry, Money amount, Map<String, Object> methodDetails) {
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, PaymentMethod.CARD, methodDetails, pan, expiry, amount);
    }

    public static PaymentProcessorRequest nonCard(UUID paymentId, PaymentMethod paymentMethod, Money amount, Map<String, Object> methodDetails) {
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, paymentMethod, methodDetails, null, null, amount);
    }
}
