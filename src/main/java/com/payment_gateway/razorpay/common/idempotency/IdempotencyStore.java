package com.payment_gateway.razorpay.common.idempotency;

import java.time.Duration;
import java.util.Optional;

public interface IdempotencyStore {

    String IN_PROGRESS = "__IN_PROGRESS__";

    void store(String key, String value, Duration ttl);

    boolean setIfAbsent(String key, Duration ttl);

    void delete(String key);

    Optional<String> get(String key);
}
