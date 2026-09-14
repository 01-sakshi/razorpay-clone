package com.payment_gateway.razorpay.common.dto;

import java.util.UUID;

public record WebhookTarget(
        UUID configId,
        String targetUrl,
        String webhookSecret) {

}
