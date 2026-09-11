package com.payment_gateway.razorpay.payment.outbox;

import com.payment_gateway.razorpay.common.enums.EventAggregateType;
import com.payment_gateway.razorpay.payment.entity.OutboxEvent;
import com.payment_gateway.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;

    public void publish(EventAggregateType aggregateType, UUID aggregateId, String eventType,
                        Map<String, Object> payload) {

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .eventType(eventType)
                .payload(payload)
                .publishedAt(Instant.now())
                .build();
        outboxEventRepository.save(outboxEvent);
    }
}
