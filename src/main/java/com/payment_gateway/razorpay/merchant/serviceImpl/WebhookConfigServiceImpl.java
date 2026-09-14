package com.payment_gateway.razorpay.merchant.serviceImpl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment_gateway.razorpay.common.util.RandomizerUtil;
import com.payment_gateway.razorpay.merchant.dto.request.WebhookConfigRequest;
import com.payment_gateway.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import com.payment_gateway.razorpay.merchant.entity.MerchantWebhookConfig;
import com.payment_gateway.razorpay.merchant.mapper.WebhookConfigMapper;
import com.payment_gateway.razorpay.merchant.repository.MerchantRepository;
import com.payment_gateway.razorpay.merchant.repository.WebhookConfigRepository;
import com.payment_gateway.razorpay.merchant.service.WebhookConfigService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookConfigServiceImpl implements WebhookConfigService {

    private final WebhookConfigRepository webhookConfigRepository;
    private final MerchantRepository merchantRepository;
    private final WebhookConfigMapper webhookConfigMapper;
    private final BytesEncryptor bytesEncryptor;

    @Override
    @Transactional
    public WebhookConfigResponse create(UUID merchantId, WebhookConfigRequest webhookConfigRequest) {
        Merchant merchant = merchantRepository.findById(merchantId).orElseThrow(
                () -> new ResourceNotFoundException("MERCHANT", "Merchant not found, merchantId: " + merchantId));

        String rawSecret = RandomizerUtil.randomBase64(32);
        byte[] rawSecretBytes = rawSecret.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedSecretBytes = bytesEncryptor.encrypt(rawSecretBytes);
        String encryptedSecret = Base64.getEncoder().encodeToString(encryptedSecretBytes);

        MerchantWebhookConfig merchantWebhookConfig = MerchantWebhookConfig.builder()
                .eventTypes(webhookConfigRequest.eventTypes())
                .targetUrl(webhookConfigRequest.targetUrl())
                .merchant(merchant)
                .webhookSecret(encryptedSecret)
                .build();

        merchantWebhookConfig = webhookConfigRepository.save(merchantWebhookConfig);
        return webhookConfigMapper.toResponse(merchantWebhookConfig, rawSecret);
    }

    @Override
    public List<WebhookConfigResponse> getAll(UUID merchantId) {
        return webhookConfigRepository.findByMerchantId(merchantId).stream()
                .map(merchantWebhookConfig -> webhookConfigMapper.toResponse(merchantWebhookConfig, null)).toList();
    }

    @Override
    public WebhookConfigResponse getById(UUID merchantId, UUID webhookConfigId) {
        return webhookConfigMapper.toResponse(fetchWebhookConfig(merchantId, webhookConfigId), null);
    }

    @Override
    @Transactional
    public WebhookConfigResponse update(UUID merchantId, UUID merchantConfigId,
            WebhookConfigRequest webhookConfigRequest) {
        MerchantWebhookConfig merchantWebhookConfig = fetchWebhookConfig(merchantId, merchantConfigId);
        merchantWebhookConfig.setTargetUrl(webhookConfigRequest.targetUrl());
        merchantWebhookConfig.setEventTypes(webhookConfigRequest.eventTypes());
        merchantWebhookConfig = webhookConfigRepository.save(merchantWebhookConfig);
        return webhookConfigMapper.toResponse(merchantWebhookConfig, null);
    }

    @Override
    @Transactional
    public void delete(UUID merchantId, UUID webhookConfigId) {
        webhookConfigRepository.deleteByMerchantIdAndId(merchantId, webhookConfigId);
    }

    private MerchantWebhookConfig fetchWebhookConfig(UUID merchantId, UUID webhookConfigId) {
        return webhookConfigRepository
                .findByMerchantIdAndId(merchantId, webhookConfigId).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Merchant Webhook Config not found, merchantId: " + merchantId));
    }

}
