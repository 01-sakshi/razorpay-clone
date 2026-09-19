package com.payment_gateway.razorpay.operations.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment_gateway.razorpay.operations.entity.SettlementPayment;
import com.payment_gateway.razorpay.operations.entity.SettlementPaymentId;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {

}
