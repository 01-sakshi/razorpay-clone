package com.payment_gateway.razorpay.merchant.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WebhookConfigResponse(
    UUID id,
    String targetUrl,
    String eventTypes,
    String webhookSecret,
    boolean enabled
) {
    
}
