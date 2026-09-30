package com.payment_gateway.razorpay.operations.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import com.payment_gateway.razorpay.common.enums.WebhookEventStatus;
import com.payment_gateway.razorpay.operations.entity.WebhookEvent;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {

    /** Selects events with the exact status and a retry timestamp strictly earlier than the cutoff for queue reconciliation. */
    List<WebhookEvent> findByEventStatusAndNextRetryAtBefore(WebhookEventStatus pending, Instant instant);

}
