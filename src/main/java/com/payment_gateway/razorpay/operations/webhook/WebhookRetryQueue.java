package com.payment_gateway.razorpay.operations.webhook;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookRetryQueue {

    @Value("app.webhook.delivery.redis-key:webhook-retry")
    private String key;

    private final StringRedisTemplate redis;

    public void enqueue(UUID webhookEventId, Instant retryAt) {
        long time = retryAt.toEpochMilli();
        redis.opsForZSet().add(key, webhookEventId.toString(), time);
        log.info("Enqueued a webhook event with id: {}", webhookEventId);
    }

    public void enqueueIfAbsent(UUID webhookEventId, Instant retryAt) {
        long time = retryAt.toEpochMilli();
        redis.opsForZSet().addIfAbsent(key, webhookEventId.toString(), time);
        log.info("Enqueued a webhook event with id: {}", webhookEventId);
    }

    public Set<UUID> pollDue(int limit) {
        long time = Instant.now().toEpochMilli();
        // rangeByScoreWithScores is to fetch records on the basis of their
        // score(timestamp)
        // whereas rangeByScore fetches records on the basis of their index.
        Set<TypedTuple<String>> due = redis.opsForZSet().rangeByScoreWithScores(key, 0,
                time, 0, limit);
        if (due == null || due.isEmpty()) {
            return Set.of();
        }
        due.forEach(tuple -> redis.opsForZSet().remove(key, tuple.getValue()));
        // return webhookEventId for all the applicable webhook events
        return due.stream().map(d -> UUID.fromString(d.getValue())).collect(Collectors.toSet());
    }
}
