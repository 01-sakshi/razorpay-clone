package com.payment_gateway.razorpay.merchant.service;

import com.payment_gateway.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest);

    List<ApiKeyResponse> list(UUID merchantId);

    String revoke(UUID merchantId, String keyId);

    ApiKeyCreateResponse rotate(UUID merchantId, String keyId);
}
