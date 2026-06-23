package com.payment_gateway.razorpay.payment.entity;

import com.payment_gateway.razorpay.common.constants.Constants;
import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "order_record"
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID merchant;      // TODO: Change column name to merchantId

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    @Column(length = 30)
    private String idempotencyKey;

    @Embedded
    private Money amount;

    @Column(nullable = true)
    private String receipt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> notes;

    /*payment attempts for the order*/
    @Builder.Default
    private int attempts = 0;

    private Instant expiresAt;

    @Builder.Default
    private String createdBy = Constants.SYSTEM;
    @Builder.Default
    private String updatedBy = Constants.SYSTEM;
    @Builder.Default
    private Instant createdAt = Instant.now();
    @Builder.Default
    private Instant updatedAt = Instant.now();

}
