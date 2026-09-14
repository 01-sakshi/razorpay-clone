package com.payment_gateway.razorpay.merchant.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.payment_gateway.razorpay.merchant.dto.response.WebhookConfigResponse;
import com.payment_gateway.razorpay.merchant.entity.MerchantWebhookConfig;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WebhookConfigMapper {

    @Mapping(target = "webhookSecret", source = "rawSecret")
    WebhookConfigResponse toResponse(MerchantWebhookConfig merchantWebhookConfig, String rawSecret);
}
