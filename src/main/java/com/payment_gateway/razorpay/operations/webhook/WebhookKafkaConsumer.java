package com.payment_gateway.razorpay.operations.webhook;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

import com.payment_gateway.razorpay.common.dto.WebhookTarget;
import com.payment_gateway.razorpay.common.enums.WebhookEventStatus;
import com.payment_gateway.razorpay.common.util.SignerUtil;
import com.payment_gateway.razorpay.merchant.api.MerchantWebhookApi;
import com.payment_gateway.razorpay.operations.entity.WebhookEvent;
import com.payment_gateway.razorpay.operations.repository.WebhookEventRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookKafkaConsumer {

    private final MerchantWebhookApi merchantWebhookApi;
    private final JsonMapper jsonMapper;
    private final SignerUtil signerUtil;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue retryQueue;
    private final WebhookDlqRecorder dlqRecorder;

    @KafkaListener(topics = {
            "${app.kafka.topics.payment:payment.events}",
            "${app.kafka.topics.order:order.events}",
            "${app.kafka.topics.refund:refund.events}",
            "${app.kafka.topics.settlement:settlement.events}"
    })
    public void listenOnWebhookEvent(ConsumerRecord<String, Map<String, Object>> consumerRecord,
            Acknowledgment acknowledgment) {

        try {
            log.info("Consumer Record: {}", consumerRecord);
            Map<String, Object> envelope = consumerRecord.value();
            Map<String, Object> payload = (Map<String, Object>) envelope.get("data");
            String eventType = (String) envelope.get("eventType");
            Object merchantIdRaw = payload.get("merchantId");

            if (merchantIdRaw == null) {
                log.error("No merchantId was found, skipping event: {}", eventType);
                acknowledgment.acknowledge();
                return;
            }

            UUID merchantId = UUID.fromString(merchantIdRaw.toString());
            List<WebhookTarget> activeConfigsForEvent = merchantWebhookApi.getActiveConfigsForEvent(merchantId,
                    eventType);
            if (activeConfigsForEvent.isEmpty()) {
                log.info("No webhook target was found, so skipping event : {}", eventType);
                acknowledgment.acknowledge();
                return;
            }

            Map<String, Object> signatureData = Map.of("event", eventType, "payload", payload);
            String signatureJson = jsonMapper.writeValueAsString(signatureData);

            for (WebhookTarget target : activeConfigsForEvent) {
                String signature = signerUtil.sign(signatureJson, target.webhookSecret());

                WebhookEvent webhookEvent = WebhookEvent.builder()
                        .eventStatus(WebhookEventStatus.PENDING)
                        .merchantId(merchantId)
                        // .lastAttemptAt(Instant.now())
                        // .lastResponseBody(signatureJson)
                        .payload(payload)
                        .targetUrl(target.targetUrl())
                        .eventType(eventType)
                        .signature(signature)
                        .nextRetryAt(Instant.now())
                        .build();
                webhookEvent = webhookEventRepository.save(webhookEvent);
                retryQueue.enqueue(webhookEvent.getId(), webhookEvent.getNextRetryAt());
                log.info("Created a webhook event with id: {}", webhookEvent.getId());
            }
            acknowledgment.acknowledge();
        } catch (DataAccessException | CannotCreateTransactionException dbDown) {
            // This is intentional — offset stays where it was, so Kafka will redeliver this
            // same message later once DB is back up
            log.error("Webhook consumer failed due to DB down, failed to process the record, offset: {}",
                    consumerRecord.offset(), dbDown);
        } catch (Exception logicError) {
            log.error("Webhook consumer failed to process the record, offset: {}", consumerRecord.offset(), logicError);
            acknowledgment.acknowledge();
            dlqRecorder.recordInDlqAfterFailedInConsumer(consumerRecord, logicError.getMessage());
        }
    }
}
