package com.payment_gateway.razorpay.merchant.service;

import com.payment_gateway.razorpay.merchant.dto.request.LoginRequest;
import com.payment_gateway.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.payment_gateway.razorpay.merchant.dto.response.LoginResponse;
import com.payment_gateway.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signUp(MerchantSignupRequest merchantSignupRequest);

    LoginResponse login(LoginRequest loginRequest);
}
