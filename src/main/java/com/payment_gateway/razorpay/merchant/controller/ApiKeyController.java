package com.payment_gateway.razorpay.merchant.controller;

import com.payment_gateway.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyResponse;
import com.payment_gateway.razorpay.merchant.security.MerchantContext;
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
@RequestMapping("/v1/merchants/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@Valid @RequestBody CreateApiKeyRequest createApiKeyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.create(merchantContext.getMerchantId(), createApiKeyRequest));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> list() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.list(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/revoke/{keyId}")
    public ResponseEntity<String> revoke(@PathVariable String keyId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.revoke(merchantContext.getMerchantId(), keyId));
    }

    @PostMapping("/rotate/{keyId}")
    public ResponseEntity<ApiKeyCreateResponse> rotate(@PathVariable String keyId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.rotate(merchantContext.getMerchantId(), keyId));
    }


}
