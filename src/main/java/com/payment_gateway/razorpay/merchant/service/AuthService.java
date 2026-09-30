package com.payment_gateway.razorpay.merchant.service;

import com.payment_gateway.razorpay.merchant.dto.request.LoginRequest;
import com.payment_gateway.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.payment_gateway.razorpay.merchant.dto.response.LoginResponse;
import com.payment_gateway.razorpay.merchant.dto.response.MerchantResponse;

public interface AuthService {
    /** Persists a merchant and owner account after rejecting an already-registered email, then returns the public profile. */
    MerchantResponse signUp(MerchantSignupRequest merchantSignupRequest);

    /** Authenticates the supplied credentials and returns a signed access token carrying the account's merchant and role claims. */
    LoginResponse login(LoginRequest loginRequest);
}
