package com.payment_gateway.razorpay.payment.mapper;

import com.payment_gateway.razorpay.payment.dto.response.PaymentResponse;
import com.payment_gateway.razorpay.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    /** Maps payment state and details to the API DTO, flattening {@code orderRecord.id} into {@code orderId}. */
    @Mapping(target = "orderId", source = "orderRecord.id")
    PaymentResponse toResponse(Payment payment);

    /** Applies the same order-ID flattening to each payment in the supplied list. */
    @Mapping(target = "orderId", source = "orderRecord.id")
    List<PaymentResponse> toResponseList(List<Payment> payments);
}
