package com.payment_gateway.razorpay.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record WebhookConfigRequest(
    @NotBlank(message = "targetUrl cannot be blank")
    @Size(max = 500)
    @Pattern(regexp = "^(https?)://.*$", message = "targetUrl must be a valid URL starting with http or https")
    String targetUrl,

    // Comma-separated fine-grained event type names (e.g. "PAYMENT_STATUS_CHANGED, REFUND_CREATED").
    // Null/blank/"ALL" subscribes to every event type.
    @Size(max = 1000)
    String eventTypes
) {
    
}
