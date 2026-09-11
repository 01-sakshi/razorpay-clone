package com.payment_gateway.razorpay.payment.outbox;

import com.payment_gateway.razorpay.common.enums.OutboxStatus;
import com.payment_gateway.razorpay.payment.entity.OutboxEvent;
import com.payment_gateway.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OutboxResultHandler {

    private final OutboxEventRepository outboxEventRepository;
    private final Integer MAX_ATTEMPTS = 3;

    /* @Transactional is not applicable for private methods */
    @Transactional
    public void handleEventPublished(OutboxEvent event) {
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(Instant.now());
        outboxEventRepository.save(event);
    }

    @Transactional
    public void handleEventFailed(OutboxEvent event, String errorMessage) {
        event.setAttempts(event.getAttempts() + 1);
        event.setLastError(errorMessage.length() < 1000 ? errorMessage : errorMessage.substring(0, 1000));
        if (event.getAttempts() >= MAX_ATTEMPTS) {
            event.setStatus(OutboxStatus.FAILED);
        }
        outboxEventRepository.save(event);
    }
}
