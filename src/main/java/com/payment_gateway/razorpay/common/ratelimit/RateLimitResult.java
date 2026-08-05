package com.payment_gateway.razorpay.common.ratelimit;

public record RateLimitResult(
        boolean isAllowed,
        int requestsRemaining,
        int retryAfterSeconds
) {

    public static RateLimitResult allowed(int requestsRemaining) {
        return new RateLimitResult(true, requestsRemaining, 0);
    }

    public static RateLimitResult denied(int retryAfterSeconds) {
        return new RateLimitResult(false, 0, retryAfterSeconds);
    }
}
