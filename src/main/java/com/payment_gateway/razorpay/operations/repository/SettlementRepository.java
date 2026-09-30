package com.payment_gateway.razorpay.operations.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment_gateway.razorpay.common.enums.SettlementStatus;
import com.payment_gateway.razorpay.operations.entity.Settlement;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

    /** Selects settlements in the requested state for bank callback simulation or operational processing. */
    List<Settlement> findByStatus(SettlementStatus transferPending);

}
