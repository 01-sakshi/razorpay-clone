package com.payment_gateway.razorpay.common.ratelimit;

public interface RateLimiter {

    /** Evaluates one request against the selected rate-limit algorithm and returns its allowance or retry delay. */
    RateLimitResult check(String key, int maxRequestsAllowed, int windowSeconds);
}
