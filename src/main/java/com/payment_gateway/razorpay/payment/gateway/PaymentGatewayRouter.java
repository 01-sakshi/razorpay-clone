package com.payment_gateway.razorpay.payment.gateway;

import com.payment_gateway.razorpay.common.enums.PaymentMethod;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentRequest;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    /*Bean created via PaymentAdapterConfig*/
    private final Map<PaymentMethod, PaymentAdapter> paymentAdapterMap;

    public PaymentResult initiate(PaymentRequest paymentRequest) {
        PaymentAdapter paymentAdapter = paymentAdapterMap.get(paymentRequest.paymentMethod());
        if (paymentAdapter == null)
            throw new IllegalArgumentException("No payment adapter found for the payment method: " + paymentRequest.paymentMethod());
        return paymentAdapter.initiate(paymentRequest);
    }

    public PaymentResult capture(PaymentMethod paymentMethod, UUID paymentId) {
        PaymentAdapter paymentAdapter = paymentAdapterMap.get(paymentMethod);
        if (paymentAdapter == null)
            throw new IllegalArgumentException("No payment adapter found for the payment method: " + paymentMethod);
        return paymentAdapter.capture(paymentId);
    }
}
