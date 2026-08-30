package com.payment_gateway.razorpay.payment.repository;

import com.payment_gateway.razorpay.payment.entity.OrderRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderRecord, UUID> {
    boolean existsByReceiptAndMerchant(String receipt, UUID merchantId);

    boolean existsByIdAndMerchant(UUID orderId, UUID merchantId);

    Optional<OrderRecord> findByIdAndMerchant(UUID orderId, UUID merchantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from OrderRecord o where o.id = :orderId and o.merchant = :merchantId")
    Optional<OrderRecord> findByIdAndMerchantForUpdate(UUID orderId, UUID merchantId);
}
