package com.payment_gateway.razorpay.payment.repository;

import com.payment_gateway.razorpay.payment.entity.OrderRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {
    boolean existsByReceiptAndMerchant(String receipt, UUID merchantId);

    boolean existsByIdAndMerchant(UUID orderId, UUID merchantId);

    Optional<OrderRecord> findByIdAndMerchant(UUID orderId, UUID merchantId);
}
