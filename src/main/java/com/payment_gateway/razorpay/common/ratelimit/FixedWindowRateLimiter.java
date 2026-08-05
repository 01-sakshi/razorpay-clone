package com.payment_gateway.razorpay.common.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "fixed")
public class FixedWindowRateLimiter implements RateLimiter {

    private final StringRedisTemplate apiKeyRedis;

    @Override
    public RateLimitResult check(String key, int maxRequestsAllowed, int windowSeconds) {
        String redisKey = "rateLimit:fixed:" + key;
        Long count = apiKeyRedis.opsForValue().increment(redisKey);

        if (count == null) return RateLimitResult.allowed(maxRequestsAllowed);   //redis is unavailable
        if (count == 1) apiKeyRedis.expire(redisKey, Duration.ofSeconds(windowSeconds)); //First API hit
        if (count > maxRequestsAllowed) {
            Long ttl = apiKeyRedis.getExpire(redisKey);
            int retryAfterSeconds = ttl != null && ttl > 0 ? ttl.intValue() : windowSeconds;
            return RateLimitResult.denied(retryAfterSeconds);
        }
        return RateLimitResult.allowed((int) (maxRequestsAllowed - count));
    }
}
