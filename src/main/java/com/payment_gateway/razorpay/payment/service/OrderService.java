package com.payment_gateway.razorpay.payment.service;

import com.payment_gateway.razorpay.payment.dto.request.CreateOrderRequest;
import com.payment_gateway.razorpay.payment.dto.response.OrderResponse;
import com.payment_gateway.razorpay.payment.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest createOrderRequest);

    OrderResponse get(UUID merchantId, UUID orderId);

    String cancel(UUID merchantId, UUID orderId);

    List<PaymentResponse> list(UUID merchantId, UUID orderId);
}
