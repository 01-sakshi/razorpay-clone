package com.payment_gateway.razorpay.operations.webhook;

import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.payment_gateway.razorpay.common.enums.WebhookEventStatus;
import com.payment_gateway.razorpay.operations.entity.DlqEvent;
import com.payment_gateway.razorpay.operations.entity.WebhookEvent;
import com.payment_gateway.razorpay.operations.repository.DlqEventRepository;
import com.payment_gateway.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookDlqRecorder {

    private final WebhookEventRepository webhookEventRepository;
    private final DlqEventRepository dlqEventRepository;

    /** Uses a new transaction to mark an exhausted delivery {@code DEAD} and persist its final error and payload. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordInDlqAfterRetryAttemptsExhausted(WebhookEvent event, String error) {
        log.info("Recording the DLQ event with event id : {}", event.getId());
        event.setEventStatus(WebhookEventStatus.DEAD);
        webhookEventRepository.save(event);

        DlqEvent dlqEvent = DlqEvent.builder()
                .webhookEvent(event)
                .merchantId(event.getMerchantId())
                .payload(event.getPayload())
                .finalError(error)
                // .movedAt(Instant.now())
                .build();
        dlqEvent = dlqEventRepository.save(dlqEvent);
    }

    /** Persists a failed Kafka envelope as a DLQ row, extracting a UUID merchant ID from {@code data} when possible. */
    public void recordInDlqAfterFailedInConsumer(ConsumerRecord<String, Map<String, Object>> consumerRecord,
            String error) {
        Map<String, Object> envelope = consumerRecord.value();
        UUID merchantId = null;
        try {
            Map<String, Object> payload = (Map<String, Object>) envelope.get("data");
            Object merchantIdRaw = payload.get("merchantId");
            if (merchantIdRaw != null) {
                merchantId = UUID.fromString(merchantIdRaw.toString());
            }
        } catch (Exception ignored) {

        }
        DlqEvent dlqEvent = DlqEvent.builder()
                .webhookEvent(null)
                .finalError(error)
                .payload(envelope != null ? envelope : Map.of())
                .merchantId(merchantId)
                .build();
        dlqEvent = dlqEventRepository.save(dlqEvent);
    }
}
