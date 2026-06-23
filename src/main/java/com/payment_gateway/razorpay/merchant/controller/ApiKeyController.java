package com.payment_gateway.razorpay.merchant.controller;

import com.payment_gateway.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyResponse;
import com.payment_gateway.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/merchants/{merchantId}/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@PathVariable UUID merchantId,
            @Valid @RequestBody CreateApiKeyRequest createApiKeyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.create(merchantId, createApiKeyRequest));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> list(@PathVariable UUID merchantId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.list(merchantId));
    }

    @DeleteMapping("/revoke/{keyId}")
    public ResponseEntity<String> revoke(@PathVariable UUID merchantId, @PathVariable String keyId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.revoke(merchantId, keyId));
    }

    @PostMapping("/rotate/{keyId}")
    public ResponseEntity<ApiKeyCreateResponse> rotate(@PathVariable UUID merchantId, @PathVariable String keyId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.rotate(merchantId, keyId));
    }


}
