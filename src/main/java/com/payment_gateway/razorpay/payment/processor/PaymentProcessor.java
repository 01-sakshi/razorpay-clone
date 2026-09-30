package com.payment_gateway.razorpay.payment.processor;

import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {
    /** Submits a normalized method-specific charge request and reports whether it is pending, successful, or failed. */
    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
