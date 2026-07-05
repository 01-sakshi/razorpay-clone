package com.payment_gateway.razorpay.payment.service;

import com.payment_gateway.razorpay.payment.dto.request.PaymentInitRequest;
import com.payment_gateway.razorpay.payment.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);

    PaymentResponse capture(UUID merchantId, UUID paymentId);
}
