package com.payment_gateway.razorpay.merchant.entity;

import com.payment_gateway.razorpay.common.entity.BaseAuditEntity;
import com.payment_gateway.razorpay.common.enums.Environment;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "api_key",
        indexes = {
                @Index(name = "idx_api_key_merchant_id", columnList = "merchant_id")
        })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiKey extends BaseAuditEntity {
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

    @Column(length = 200)
    private String previousKeySecretHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Environment environment;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    private Instant lastUsedAt;
    private Instant rotatedAt;
    private Instant gracePeriodExpiresAt;
}
