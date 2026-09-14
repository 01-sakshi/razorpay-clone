package com.payment_gateway.razorpay.operations.webhook;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.payment_gateway.razorpay.common.enums.WebhookEventStatus;
import com.payment_gateway.razorpay.operations.entity.WebhookEvent;
import com.payment_gateway.razorpay.operations.repository.WebhookEventRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookDeliverExecutor {

    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue webhookRetryQueue;
    private final WebhookDlqRecorder dlqRecorder;
    private final RestClient restClient;
    private final Integer MAX_ATTEMPTS = 7;
    private static final List<Duration> BACKOFF = List.of(
            Duration.ofMinutes(1), Duration.ofMinutes(5), Duration.ofMinutes(30),
            Duration.ofHours(2), Duration.ofHours(8), Duration.ofHours(24));

    @Value("${app.webhook.delivery.signature-header:X-Razorpay-Signature}")
    private String signatureHeader;

    @Transactional
    public void deliver(UUID webhookEventId) {
        Optional<WebhookEvent> optionalEvent = webhookEventRepository.findById(webhookEventId);

        if (optionalEvent == null) {
            log.error("No webhook event found, event id: {}", webhookEventId);
            return;
        }

        WebhookEvent event = optionalEvent.get();
        if (event.getEventStatus().equals(WebhookEventStatus.DELIVERED)
                || event.getEventStatus().equals(WebhookEventStatus.DEAD)) {
            log.error("Cannot deliver the event in status: {}", event.getEventStatus());
            return;
        }

        event.setAttempts(event.getAttempts() + 1);
        event.setLastAttemptAt(Instant.now());

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri(event.getTargetUrl())
                    .header(signatureHeader, event.getSignature())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("event", event.getEventType(), "payload", event.getPayload()))
                    .retrieve()
                    .toBodilessEntity(); // returns a response entity without body

            int statusCode = response.getStatusCode().value();
            event.setLastResponseCode(statusCode);

            if (response.getStatusCode().is2xxSuccessful()) {
                event.setDeliveredAt(Instant.now());
                event.setEventStatus(WebhookEventStatus.DELIVERED);
                event = webhookEventRepository.save(event);
                log.info("Successfully called the merchant for webhook event: {}", webhookEventId);
                return;
            }
            event.setLastResponseBody("HTTP : " + statusCode);
            handleAttemptFailed(event, event.getLastResponseBody());
        } catch (RestClientException e) {
            event.setLastResponseBody(e.getMessage());
            handleAttemptFailed(event, event.getLastResponseBody());
            log.error("Got RestClientException : ", e);
        }
    }

    private void handleAttemptFailed(WebhookEvent event, String error) {
        if (event.getAttempts() >= MAX_ATTEMPTS) {
            // move to Dead Letter Queue(DLQ)
            dlqRecorder.recordInDlqAfterRetryAttemptsExhausted(event, error);
            return;
        }

        Duration score = BACKOFF.get(event.getAttempts() - 1);
        Instant nextRetryAt = Instant.now().plus(score);
        event.setEventStatus(WebhookEventStatus.FAILED);
        event.setNextRetryAt(nextRetryAt);
        webhookEventRepository.save(event);
        webhookRetryQueue.enqueue(event.getId(), nextRetryAt); // Move to redis for retrying
        log.error("Handling attempt failed for webhook event : {} with attempts : {}, next retry at : {}",
                event.getId(), event.getAttempts(), event.getNextRetryAt());
    }
}
