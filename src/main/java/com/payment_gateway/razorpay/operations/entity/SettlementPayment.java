package com.payment_gateway.razorpay.operations.entity;

import com.payment_gateway.razorpay.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "settlement_payment"
)
@Getter
@Setter
public class SettlementPayment extends BaseAuditEntity {

    @EmbeddedId
    private SettlementPaymentId settlementPaymentId;

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;
}
