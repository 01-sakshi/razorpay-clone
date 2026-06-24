package com.payment_gateway.razorpay.payment.entity;

import com.payment_gateway.razorpay.common.enums.PaymentEvent;
import com.payment_gateway.razorpay.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "payment_transition_log",
        indexes = {
                @Index(name = "idx_payment_transition_payment_id", columnList = "payment_id")
        }
)
@Getter
@Setter
public class PaymentTransitionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus toStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentEvent event;

    /*could be system(razorpay itself) or admin(merchant) does it from admin dashboard*/
    @Column(length = 50, nullable = false)
    private String actor;

    private Instant createdAt;  //occuredAt
}
