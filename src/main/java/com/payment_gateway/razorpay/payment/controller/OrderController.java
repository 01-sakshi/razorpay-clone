package com.payment_gateway.razorpay.payment.controller;

import com.payment_gateway.razorpay.merchant.security.MerchantContext;
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
@RequestMapping("/v1/order")    //TODO: Make it orders
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody CreateOrderRequest createOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.create(merchantContext.getMerchantId(), createOrderRequest));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> get(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.get(merchantContext.getMerchantId(), orderId));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancel(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.cancel(merchantContext.getMerchantId(), orderId));
    }

    @GetMapping("/{orderId}/payments")
    public ResponseEntity<List<PaymentResponse>> list(@PathVariable UUID orderId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderService.list(merchantContext.getMerchantId(), orderId));
    }
}
