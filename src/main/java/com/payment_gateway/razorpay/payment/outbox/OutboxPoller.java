package com.payment_gateway.razorpay.payment.outbox;

import com.payment_gateway.razorpay.common.config.KafkaProperties;
import com.payment_gateway.razorpay.common.enums.OutboxStatus;
import com.payment_gateway.razorpay.payment.entity.OutboxEvent;
import com.payment_gateway.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxResultHandler resultHandler;

    @Scheduled(fixedDelay = 5000)
    public void poll() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (OutboxEvent event : pendingEvents) {
            try {
                String topic = kafkaProperties.topicFor(event.getAggregateType());
                String key = extractMerchantId(event.getPayload());
                log.info("Event payload: {}", event.getPayload());

                Map<String, Object> envelop = Map.of(
                        "eventType", event.getEventType(),
                        "aggregateType", event.getAggregateType(),
                        "aggregateId", event.getAggregateId(),
                        "data", event.getPayload());

                kafkaTemplate.send(topic, key, envelop).get(5, TimeUnit.SECONDS);
                resultHandler.handleEventPublished(event);
            } catch (Exception e) {
                log.error("Outbox event failed eventId: {} attempts: {}", event.getId(), event.getAttempts(), e);
                resultHandler.handleEventFailed(event, e.getMessage());
            }
        }
    }

    private String extractMerchantId(Map<String, Object> payload) {
        Object merchantId = payload.get("merchantId");
        return merchantId != null ? merchantId.toString() : "unknown";
    }
}
