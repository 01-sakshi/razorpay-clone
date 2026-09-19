package com.payment_gateway.razorpay.payment.api;

import java.util.List;
import java.util.UUID;

import com.payment_gateway.razorpay.payment.entity.Payment;

public interface PaymentLookupService {
    List<Payment> findUnsettledCapturedPayments(UUID merchantId);
}
