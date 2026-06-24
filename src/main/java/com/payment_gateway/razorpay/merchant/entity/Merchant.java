package com.payment_gateway.razorpay.merchant.entity;

import com.payment_gateway.razorpay.common.constants.Constants;
import com.payment_gateway.razorpay.common.enums.BusinessType;
import com.payment_gateway.razorpay.common.enums.MerchantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "merchant",
        indexes = {
                @Index(name = "idx_merchant_status", columnList = "status")
        })
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    private String gstId;

    @Column(length = 50)
    private String businessName;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 200)
    private String websiteUrl;

    @Column(length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MerchantStatus status = MerchantStatus.PENDING_KYC;

    @Column(length = 20)
    private String panId;   //settlementAccountNumber

    @Column(length = 200)
    private String settlementBankAccount;

    @Column(length = 200)
    private String settlementBankAccountHolderName;

    @Column(length = 20)
    private String settlementBankIfsc;

    @Builder.Default
    private String createdBy = Constants.SYSTEM;
    @Builder.Default
    private String updatedBy = Constants.SYSTEM;

    @Builder.Default
    private Instant createdAt = Instant.now();
    @Builder.Default
    private Instant updatedAt = Instant.now();
}
