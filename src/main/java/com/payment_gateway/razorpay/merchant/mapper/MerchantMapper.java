package com.payment_gateway.razorpay.merchant.mapper;

import com.payment_gateway.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.payment_gateway.razorpay.merchant.dto.response.MerchantResponse;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    @Mapping(target = "merchantStatus", source = "status")
    MerchantResponse toResponse(Merchant merchant);

    Merchant toEntityFromSignUpRequest(MerchantSignupRequest merchantSignupRequest);
}
