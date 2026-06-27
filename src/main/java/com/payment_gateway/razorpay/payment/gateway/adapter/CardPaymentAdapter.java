package com.payment_gateway.razorpay.payment.gateway.adapter;

import com.payment_gateway.razorpay.payment.gateway.PaymentAdapter;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentRequest;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentResult;
import org.springframework.stereotype.Component;

@Component
public class CardPaymentAdapter implements PaymentAdapter {

    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {
        return null;
    }
}
