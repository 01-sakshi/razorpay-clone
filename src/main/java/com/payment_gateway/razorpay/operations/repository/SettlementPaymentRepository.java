package com.payment_gateway.razorpay.operations.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment_gateway.razorpay.operations.entity.SettlementPayment;
import com.payment_gateway.razorpay.operations.entity.SettlementPaymentId;

import java.util.List;
import java.util.UUID;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, SettlementPaymentId> {
	List<SettlementPayment> findAllBySettlementPaymentId_SettlementId(UUID settlementId);

}
