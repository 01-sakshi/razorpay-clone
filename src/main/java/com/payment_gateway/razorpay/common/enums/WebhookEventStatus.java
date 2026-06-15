package com.payment_gateway.razorpay.common.enums;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

public enum WebhookEventStatus {
    PENDING,
    DELIVERED,
    FAILED,
    DEAD
}