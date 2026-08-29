package com.payment_gateway.razorpay.common.idempotency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor    //RequiredArgsConstructor creates a constructor only for final fields and fields marked as @NonNull
@Slf4j
public class RedisIdempotencyStore implements IdempotencyStore {

    private final StringRedisTemplate redis;
    String prefix = "idempotency:";

    @Override
    public void store(String key, String value, Duration ttl) {
        try {
            redis.opsForValue().set(prefix + key, value, ttl);
        } catch (DataAccessException e) {
            log.error("Failed to store key: {}", key, e);
        }
    }

    @Override
    public boolean setIfAbsent(String key, Duration ttl) {
        try {
            return redis.opsForValue().setIfAbsent(prefix + key, IN_PROGRESS, ttl);
        } catch (DataAccessException e) {
            log.error("Idempotency Store unavailable, failing open for key: {}", key, e);
            return true;
        }
    }

    @Override
    public void delete(String key) {
        try {
            redis.delete(prefix + key);
        } catch (DataAccessException e) {
            log.error("Failed to remove key from redis, key: {}", key, e);
        }
    }

    @Override
    public Optional<String> get(String key) {
        try {
            return Optional.ofNullable(redis.opsForValue().get(prefix + key));
        } catch (DataAccessException e) {
            log.error("Failed to fetch key from redis, key: {}", key, e);
            return Optional.empty();
        }
    }
}
