package com.payment_gateway.razorpay.merchant.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment_gateway.razorpay.merchant.dto.request.WebhookConfigRequest;
import com.payment_gateway.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.payment_gateway.razorpay.merchant.security.MerchantContext;
import com.payment_gateway.razorpay.merchant.service.WebhookConfigService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/merchants/webhooks")
public class WebhookConfigController {

    private final MerchantContext merchantContext;
    private final WebhookConfigService webhookConfigService;

    @PostMapping(path = "/create")
    public ResponseEntity<WebhookConfigResponse> create(@Valid @RequestBody WebhookConfigRequest webhookConfigRequest) {
        return ResponseEntity.ok(webhookConfigService.create(merchantContext.getMerchantId(), webhookConfigRequest));
    }

    @GetMapping
    public ResponseEntity<List<WebhookConfigResponse>> getAll() {
        return ResponseEntity.ok(webhookConfigService.getAll(merchantContext.getMerchantId()));
    }

    @GetMapping("/{configId}")
    public ResponseEntity<WebhookConfigResponse> getById(@PathVariable UUID configId) {
        return ResponseEntity.ok(webhookConfigService.getById(merchantContext.getMerchantId(), configId));
    }

    @PutMapping("/{configId}")
    public ResponseEntity<WebhookConfigResponse> update(@PathVariable UUID configId,
            @Valid @RequestBody WebhookConfigRequest webhookConfigRequest) {
        return ResponseEntity
                .ok(webhookConfigService.update(merchantContext.getMerchantId(), configId, webhookConfigRequest));
    }

    @DeleteMapping("/{configId}")
    public ResponseEntity<Void> delete(@PathVariable UUID configId) {
        webhookConfigService.delete(merchantContext.getMerchantId(), configId);
        return ResponseEntity.noContent().build();
    }
}
