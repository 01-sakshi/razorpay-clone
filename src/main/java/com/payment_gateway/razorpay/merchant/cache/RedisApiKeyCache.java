package com.payment_gateway.razorpay.merchant.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisApiKeyCache implements ApiKeyCache {

    private final StringRedisTemplate stringRedisTemplate;
    private final Duration TTL = Duration.ofMinutes(5);
    private final ObjectMapper objectMapper;
    private final String API_KEY_PREFIX = "apiKey:";

    @Override
    public Optional<ApiKeyCacheEntry> get(String keyId) {
        try {
            String json = stringRedisTemplate.opsForValue().get(API_KEY_PREFIX + keyId);
            if (json == null) return Optional.empty();
            return Optional.of(objectMapper.readValue(json, ApiKeyCacheEntry.class));
        } catch (Exception exception) {
            log.error("ApiKey cache read failed for keyId: {}", keyId);
            return Optional.empty();
        }
    }

    @Override
    public void put(String keyId, ApiKeyCacheEntry apiKeyCacheEntry) {
        try {
            stringRedisTemplate.opsForValue().set(API_KEY_PREFIX + keyId,
                    objectMapper.writeValueAsString(apiKeyCacheEntry), TTL);
        } catch (Exception exception) {
            log.error("ApiKey cache write failed for keyId: {}", keyId);
        }
    }

    @Override
    public void evict(String keyId) {
        try {
            stringRedisTemplate.delete(API_KEY_PREFIX + keyId);
        } catch (Exception exception) {
            log.error("ApiKey cache delete failed for keyId: {}", keyId);
        }
    }
}
