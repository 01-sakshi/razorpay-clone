package com.payment_gateway.razorpay.merchant.cache;

import java.util.Optional;

public interface ApiKeyCache {

    public Optional<ApiKeyCacheEntry> get(String keyId);

    public void put(String KeyId, ApiKeyCacheEntry apiKeyCacheEntry);

    public void evict(String keyId);
}
