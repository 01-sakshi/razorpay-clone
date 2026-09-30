package com.payment_gateway.razorpay.payment.api;

import java.util.List;
import java.util.UUID;

import com.payment_gateway.razorpay.payment.entity.Payment;

public interface PaymentLookupService {
    /** Finds captured payments for one merchant for settlement processing; results are locked by the repository query. */
    List<Payment> findUnsettledCapturedPayments(UUID merchantId);
}
