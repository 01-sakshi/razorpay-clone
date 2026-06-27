package com.payment_gateway.razorpay.payment.processor.dto;

import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod paymentMethod,
        Map<String, Object> methodDetails,
//        String pan,
//        String expiry,      //Doubt: expiryMonth or expiryYear or both?
        Money amount
) {
}
