package com.payment_gateway.razorpay.payment.entity;

import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.PaymentMethod;
import com.payment_gateway.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "payment"
)
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderRecord orderRecord;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.CREATED;

    @Column(nullable = false)
    private UUID merchantId;

    @Embedded
    private Money amount;

    @Column(nullable = false)
    private PaymentMethod method;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> methodDetails;

    @Column(nullable = false, length = 30)
    private String idempotencyKey;  //Doubt: Why wasn't it added for OrderRecord?

    @Column(length = 100)
    private String bankReference;   //UTR details
    @Column(length = 20)
    private String errorCode;
    @Column(length = 255)
    private String errorDescription;

    private Instant authorizedAt;
    private Instant capturedAt;
    private Instant failedAt;
    private Instant refundedAt;
    private Instant settledAt;

    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    private String createdBy;
}
