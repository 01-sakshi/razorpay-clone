package com.payment_gateway.razorpay.merchant.api;

import java.util.List;
import java.util.UUID;

import com.payment_gateway.razorpay.common.dto.WebhookTarget;

public interface MerchantWebhookApi {
    List<WebhookTarget> getActiveConfigsForEvent(UUID merchantId, String eventType);
}
