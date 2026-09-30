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

    /**
     * Maps the entity's stored {@code keySecretHash} into the response's {@code keySecret} property; this mapper does
     * not have the raw secret generated during initial key creation.
     */
    @Mapping(source = "keySecretHash", target = "keySecret")
    ApiKeyCreateResponse toApiKeyCreateResponse(ApiKey apiKey);

    /** Maps key metadata to list responses, whose DTO omits secret and hash fields. */
    List<ApiKeyResponse> toApikeyResponseList(List<ApiKey> apiKey);
}
