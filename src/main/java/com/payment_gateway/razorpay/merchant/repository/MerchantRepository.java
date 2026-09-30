package com.payment_gateway.razorpay.merchant.repository;

import com.payment_gateway.razorpay.common.enums.MerchantStatus;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    /** Looks up the merchant whose globally unique signup email matches the supplied value. */
    Merchant findByEmail(String email);

    /** Selects only merchant IDs for the requested status, avoiding loading full merchant entities for batch work. */
    List<UUID> findAllIdsByStatus(MerchantStatus active);

}
