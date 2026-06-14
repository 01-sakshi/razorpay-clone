package com.payment_gateway.razorpay.merchant.entity;

import com.payment_gateway.razorpay.common.enums.Environment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_key")
@Getter
@Setter
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, unique = true, length = 50)
    private String keyId;

    @Column(nullable = false, length = 200)
    private String keySecretHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Environment environment;

    @Column(nullable = false)
    //@Builder.Default
    private Boolean enabled = true;

    private Instant lastUsedAt;
    private Instant rotatedAt;
    private Instant gracePeriodExpiresAt;

    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
