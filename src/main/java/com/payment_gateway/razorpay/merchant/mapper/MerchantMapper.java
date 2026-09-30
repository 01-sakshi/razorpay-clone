package com.payment_gateway.razorpay.merchant.mapper;

import com.payment_gateway.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.payment_gateway.razorpay.merchant.dto.response.MerchantResponse;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {

    /** Maps the persisted merchant profile to the public DTO, explicitly exposing entity {@code status} as {@code merchantStatus}. */
    @Mapping(target = "merchantStatus", source = "status")
    MerchantResponse toResponse(Merchant merchant);

    /** Creates a merchant entity from signup fields; persistence-generated identity and defaults remain entity concerns. */
    Merchant toEntityFromSignUpRequest(MerchantSignupRequest merchantSignupRequest);
}
