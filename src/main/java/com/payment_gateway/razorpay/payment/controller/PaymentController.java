package com.payment_gateway.razorpay.payment.controller;

import com.payment_gateway.razorpay.payment.dto.request.PaymentInitRequest;
import com.payment_gateway.razorpay.payment.dto.response.PaymentResponse;
import com.payment_gateway.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    UUID merchantId = UUID.fromString("329e7a3d-6649-45b8-9cd8-cd0e0e94612e");   //TODO: dummy merchantId, later will be handled as part of spring security

    @PostMapping()
    public ResponseEntity<PaymentResponse> initiate(@Valid @RequestBody PaymentInitRequest paymentInitRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(merchantId, paymentInitRequest));
    }

    @PostMapping("/capture/{paymentId}")
    public ResponseEntity<PaymentResponse> capture(@PathVariable UUID paymentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(paymentService.capture(merchantId, paymentId));
    }
}
