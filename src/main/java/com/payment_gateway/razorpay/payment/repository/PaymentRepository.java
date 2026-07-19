package com.payment_gateway.razorpay.payment.repository;

import com.payment_gateway.razorpay.common.enums.PaymentStatus;
import com.payment_gateway.razorpay.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByOrderRecordId(UUID orderId);

    Optional<Payment> findByIdAndMerchantId(UUID paymentId, UUID merchantId);

    List<Payment> findByStatusAndUpdatedAtBefore(PaymentStatus paymentStatus, Instant globalWindow);
}
