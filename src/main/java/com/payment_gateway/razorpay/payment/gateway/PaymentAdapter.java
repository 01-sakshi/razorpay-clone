package com.payment_gateway.razorpay.payment.gateway;

import com.payment_gateway.razorpay.payment.gateway.dto.PaymentRequest;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentResult;

import java.util.UUID;

public interface PaymentAdapter {

    /** Submits the method-specific charge and normalizes the processor outcome as pending, success, or failure. */
    PaymentResult initiate(PaymentRequest paymentRequest);

    /** Requests capture for the payment ID using this adapter's method-specific gateway. */
    PaymentResult capture(UUID paymentId);
}
