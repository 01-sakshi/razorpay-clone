package com.payment_gateway.razorpay.vault.repository;

import com.payment_gateway.razorpay.vault.entity.CardToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CardTokenRepository extends JpaRepository<CardToken, UUID> {
    /** Resolves the exact opaque token only when {@code revokedAt} is null; the query itself does not scope by merchant. */
    Optional<CardToken> findByTokenAndRevokedAtNull(String token);
}
