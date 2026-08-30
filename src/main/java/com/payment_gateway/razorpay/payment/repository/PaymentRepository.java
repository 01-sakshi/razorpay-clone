package com.payment_gateway.razorpay.payment.repository;

import com.payment_gateway.razorpay.common.enums.PaymentStatus;
import com.payment_gateway.razorpay.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findAllByOrderRecordId(UUID orderId);

    Optional<Payment> findByIdAndMerchantId(UUID paymentId, UUID merchantId);

    @Query("select p from Payment p where p.id = :paymentId and p.merchantId = :merchantId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findByIdAndMerchantIdForUpdate(UUID paymentId, UUID merchantId);

    List<Payment> findByStatusAndUpdatedAtBefore(PaymentStatus paymentStatus, Instant globalWindow);

    @Query("select p from Payment p where p.id = :paymentId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findByIdForUpdate(UUID paymentId);
}
