package com.payment_gateway.razorpay.vault.service;

import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.payment_gateway.razorpay.vault.dto.request.TokenizeRequest;
import com.payment_gateway.razorpay.vault.dto.response.TokenizeResponse;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponse tokenize(TokenizeRequest tokenizeRequest, UUID merchantId);

    PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails);
}
