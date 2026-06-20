package com.payment_gateway.razorpay.vault.entity;

import com.payment_gateway.razorpay.payment.entity.OrderRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "vault_card"
)
@Getter
@Setter
public class VaultCard {

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

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false, length = 2)
    private String expiryMonth;

    @Column(nullable = false, length = 4)
    private String expiryYear;

    @Column(nullable = false)
    private String cardHolderName;

    private Instant deletedAt;

    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    private String createdBy;
}
