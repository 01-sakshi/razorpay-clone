package com.payment_gateway.razorpay.payment.repository;

import com.payment_gateway.razorpay.common.enums.OutboxStatus;
import com.payment_gateway.razorpay.payment.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    /** Returns all events in the requested status oldest-first so the poller preserves enqueue order. */
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus outboxStatus);
}
