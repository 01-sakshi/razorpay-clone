package com.payment_gateway.razorpay.payment.controller;

import com.payment_gateway.razorpay.payment.dto.request.CreateOrderRequest;
import com.payment_gateway.razorpay.payment.dto.response.OrderResponse;
import com.payment_gateway.razorpay.payment.dto.response.PaymentResponse;
import com.payment_gateway.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    UUID merchantId = UUID.fromString("329e7a3d-6649-45b8-9cd8-cd0e0e94612e");   //TODO: dummy merchantId, later will be handled as part of spring security

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(merchantId, createOrderRequest));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> get(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.get(merchantId, orderId));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancel(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.cancel(merchantId, orderId));
    }

    @GetMapping("/{orderId}/payments")
    public ResponseEntity<List<PaymentResponse>> list(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.list(merchantId, orderId));
    }
}
