package com.payment_gateway.razorpay.payment.entity;

import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.RefundStatus;
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
        name = "refund",
        indexes = {
                @Index(name = "idx_refund_status", columnList = "status"),
        }
)
@Getter
@Setter
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Embedded
    private Money amount;

    @Column(nullable = false)
    private UUID merchantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefundStatus status = RefundStatus.PENDING;

    @Column(length = 100)
    private String bankReference;   //UTR details
    @Column(length = 20)
    private String errorCode;
    @Column(length = 255)
    private String errorDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> notes;  //refund comments/reason

    private Instant processedAt;    //when was refund processed
    private Instant createdAt;
    private String createdBy;
}
