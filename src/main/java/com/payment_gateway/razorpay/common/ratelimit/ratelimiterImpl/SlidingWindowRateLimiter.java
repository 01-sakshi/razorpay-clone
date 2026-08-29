package com.payment_gateway.razorpay.common.ratelimit.ratelimiterImpl;

import com.payment_gateway.razorpay.common.ratelimit.RateLimitResult;
import com.payment_gateway.razorpay.common.ratelimit.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/*
New Request
     ↓
Calculate current 60-sec window
     ↓
Remove requests older than 60 sec
     ↓
Count remaining requests
     ↓
Count >= 5?
   /      \
YES        NO
   ↓          ↓
Find oldest  Add current request
request       ↓
    ↓        Allow request
Calculate
retry time
   ↓
Deny request
 */

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "sliding")
public class SlidingWindowRateLimiter implements RateLimiter {

    private final StringRedisTemplate redis;

    @Override
    public RateLimitResult check(String key, int maxRequestsAllowed, int windowSeconds) {
        //New Request - key

        //Calculate current 60-sec window
        long nowMs = System.currentTimeMillis();
        long floorMs = nowMs - windowSeconds * 1000;

        String redisKey = "ratelimit:sliding:" + key;

        var zset = redis.opsForZSet();  // ZSet = Redis Sorted Set

        //Remove requests older than 60 sec
        zset.removeRangeByScore(redisKey, Double.NEGATIVE_INFINITY, floorMs);

        //Count remaining requests
        Long count = zset.zCard(redisKey);
        long current = count != null ? count : 0;

        if (current >= maxRequestsAllowed) { //Count >= maxRequestAllowed = Yes
            //Find Oldest Request
            var oldest = zset.rangeWithScores(redisKey, 0, 0);
            int retryAfter = 1;

            if ((oldest != null && !oldest.isEmpty())) {
                Double oldestScore = oldest.iterator().next().getScore();
                if (oldestScore != null) {
                    long windowExpiresMs = oldestScore.longValue() + windowSeconds * 1000;
                    //Calculate retry time
                    retryAfter = (int) Math.ceil((windowExpiresMs - nowMs) / 1000.0);
                }
            }
            //Deny Request
            return RateLimitResult.denied(retryAfter);
        }

        //Count >= maxRequestAllowed = NO
        //Add current request
        zset.add(redisKey, UUID.randomUUID().toString(), nowMs);
        redis.expire(redisKey, Duration.ofSeconds(windowSeconds + 1));  //Doubt
        //Allow request
        return RateLimitResult.allowed((int) (maxRequestsAllowed - current - 1));
    }
}
