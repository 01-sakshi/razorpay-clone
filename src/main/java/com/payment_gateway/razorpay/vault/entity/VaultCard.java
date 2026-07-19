package com.payment_gateway.razorpay.vault.entity;

import com.payment_gateway.razorpay.common.entity.BaseAuditEntity;
import com.payment_gateway.razorpay.common.enums.CardBrand;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "vault_card"
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaultCard extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private byte[] encryptedPan;

    @Column(nullable = false)
    private byte[] encryptedDek;

    @Column(nullable = false, length = 6)
    private String bin;

    @Column(nullable = false, length = 4)
    private String lastFour;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CardBrand brand;

    @Column(nullable = false, length = 2)
    private String expiryMonth;

    @Column(nullable = false, length = 4)
    private String expiryYear;

    @Column(nullable = false)
    private String cardHolderName;

    private Instant deletedAt;
}
