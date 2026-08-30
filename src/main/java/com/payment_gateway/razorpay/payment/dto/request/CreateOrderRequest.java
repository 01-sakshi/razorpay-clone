package com.payment_gateway.razorpay.payment.dto.request;

import com.payment_gateway.razorpay.common.entity.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;

public record CreateOrderRequest(
        Map<String, Object> notes,
        Instant expiresAt,
        String receipt,     //This is order-id at merchant's end
        Money amount,

        @Valid //To make @Size and @Email annotations work put in CustomerDetails record
        CustomerDetails customer
) {

    public record CustomerDetails(
            @Size(max = 50)
            String name,

            @Size(max = 20)
            String phone,

            @Email
            @Size(max = 30)
            String email
    ) {

    }
}
