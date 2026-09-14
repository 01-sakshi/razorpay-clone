package com.payment_gateway.razorpay.operations.webhook;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.payment_gateway.razorpay.common.enums.WebhookEventStatus;
import com.payment_gateway.razorpay.operations.entity.WebhookEvent;
import com.payment_gateway.razorpay.operations.repository.WebhookEventRepository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookDeliveryScheduler {

    private final WebhookRetryQueue retryQueue;
    private final WebhookDeliverExecutor webhookDeliverExecutor;
    private final WebhookEventRepository webhookEventRepository;
    private ExecutorService virtualExecutorService;

    @PostConstruct
    void init() {
        virtualExecutorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @PreDestroy
    void shutdown() {
        virtualExecutorService.shutdown();
    }

    @Value("${app.webhook.delivery.poll-batch-size:100}")
    private int batchSize;

    @Scheduled(fixedDelay = 5000)
    public void pollAndDeliver() {
        Set<UUID> pollDue = retryQueue.pollDue(batchSize);
        if (pollDue == null || pollDue.isEmpty()) {
            return;
        }
        for (UUID webhookEventId : pollDue) {
            virtualExecutorService.submit(() -> {
                webhookDeliverExecutor.deliver(webhookEventId);
            });
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void reconcileFromDatabase() {
        List<WebhookEvent> events = webhookEventRepository
                .findByEventStatusAndNextRetryAtBefore(WebhookEventStatus.PENDING, Instant.now());
        events.forEach(event -> retryQueue.enqueueIfAbsent(event.getId(), event.getNextRetryAt()));
    }
}
