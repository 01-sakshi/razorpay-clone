package com.payment_gateway.razorpay.payment.processor;

import com.payment_gateway.razorpay.common.enums.PaymentMethod;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        PaymentProcessor paymentProcessor = paymentProcessorMap.get(request.paymentMethod());
        if(paymentProcessor == null) {
            throw new IllegalArgumentException("No payment router found for the payment method: " + request.paymentMethod());
        }
        return paymentProcessor.charge(request);
    }
}
