package com.payment_gateway.razorpay.merchant.service;

import java.util.List;
import java.util.UUID;

import com.payment_gateway.razorpay.merchant.dto.request.WebhookConfigRequest;
import com.payment_gateway.razorpay.merchant.dto.response.WebhookConfigResponse;

public interface WebhookConfigService {
    
    WebhookConfigResponse create(UUID merchantId, WebhookConfigRequest webhookConfigRequest);
    List<WebhookConfigResponse> getAll(UUID merchantId);
    WebhookConfigResponse getById(UUID merchantId, UUID webhookConfigId);
    WebhookConfigResponse update(UUID merchantId, UUID merchantConfigId, WebhookConfigRequest webhookConfigRequest);
    void delete(UUID merchantId, UUID webhookConfigId);
}
