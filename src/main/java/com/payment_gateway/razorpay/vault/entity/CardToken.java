package com.payment_gateway.razorpay.vault.entity;

import com.payment_gateway.razorpay.payment.entity.OrderRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "card_token"
)
@Getter
@Setter
public class CardToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String token;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    private UUID customerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vault_card_id", nullable = false)
    private VaultCard vaultCard;        //For one card, multiple card tokens can be generated for one or more merchants

    private Instant revokedAt;

}
