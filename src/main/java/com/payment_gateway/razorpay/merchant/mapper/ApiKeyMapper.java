package com.payment_gateway.razorpay.merchant.mapper;

import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyCreateResponse;
import com.payment_gateway.razorpay.merchant.dto.response.ApiKeyResponse;
import com.payment_gateway.razorpay.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {

    @Mapping(target = "keySecret", source = "keySecretHash")
    ApiKeyCreateResponse toApiKeyCreateResponse(ApiKey apiKey);

    List<ApiKeyResponse> toApikeyResponseList(List<ApiKey> apiKey);
}
