package com.payment_gateway.razorpay.merchant.repository;

import com.payment_gateway.razorpay.common.enums.MerchantStatus;
import com.payment_gateway.razorpay.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    Merchant findByEmail(String email);

    List<UUID> findAllIdsByStatus(MerchantStatus active);

}
