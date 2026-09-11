package com.payment_gateway.razorpay.payment.entity;

import com.payment_gateway.razorpay.common.entity.BaseAuditEntity;
import com.payment_gateway.razorpay.common.enums.EventAggregateType;
import com.payment_gateway.razorpay.common.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private EventAggregateType aggregateType;   //Like: Order, Payment, Refund

    @Column(nullable = false)
    private UUID aggregateId;   //Like: Order ID, Payment ID - based on aggregate type

    @Column(nullable = false, length = 50)
    private String eventType;   //Like: Order Created, Order Shipped, Payment Attempted

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Builder.Default
    @Column(nullable = false)
    private Integer attempts = 0;

    @Column(length = 1000)
    private String lastError;

    private Instant publishedAt;
}
