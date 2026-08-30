package com.payment_gateway.razorpay.merchant.service;

import java.util.UUID;

public interface CustomerService {

    UUID findOrCreate(UUID merchantId, String name, String phone, String email);
}
