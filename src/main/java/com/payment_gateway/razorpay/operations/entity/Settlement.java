package com.payment_gateway.razorpay.operations.entity;

import com.payment_gateway.razorpay.common.entity.BaseAuditEntity;
import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "settlement"
)
@Getter
@Setter
public class Settlement extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "gross-amount-units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "gross-currency", nullable = false))
    })
    private Money grossAmount;

    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "refund-amount-units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "refund-currency", nullable = false))
    })
    private Money refundAmount;

    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "fee-amount-units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "fee-currency", nullable = false))
    })
    private Money feeAmount;

    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "gst-amount-units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "gst-currency", nullable = false))
    })
    private Money gstAmount;

    @AttributeOverrides({
            @AttributeOverride(name = "amountUnits", column = @Column(name = "net-amount-units", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "net-currency", nullable = false))
    })
    private Money netAmount;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status;

    private String bankReference;

    private Instant settledAt;  //processedAt
}
