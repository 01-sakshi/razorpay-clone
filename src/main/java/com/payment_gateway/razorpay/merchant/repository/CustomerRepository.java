package com.payment_gateway.razorpay.merchant.repository;

import com.payment_gateway.razorpay.merchant.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByEmailAndMerchantId(String email, UUID merchantId);
}
