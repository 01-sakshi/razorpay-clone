package com.payment_gateway.razorpay.merchant.dto.request;

import com.payment_gateway.razorpay.common.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantSignupRequest(
        @NotNull(message = "Name is required")
        @Size(max = 50, message = "Name cannot be greater than 50 characters")
        String name,

        @NotNull(message = "Email is required")
        @Email
        String email,

        @NotNull(message = "Password is required")
        @Size(min = 12, message = "Password should be at least 12 characters")
        String password,

        @NotNull(message = "Business Name is required")
        @Size(max = 20, message = "Business Name cannot be greater than 20 characters")
        String businessName,

//        @NotNull(message = "Business Type is required")
        BusinessType businessType
) {

}
