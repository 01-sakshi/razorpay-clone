//package com.payment_gateway.razorpay.operations.entity;
//
//import com.payment_gateway.razorpay.common.enums.SettlementStatus;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.time.Instant;
//import java.util.UUID;
//
//@Entity
//@Table(
//        name = "settlement"
//)
//@Getter
//@Setter
//public class Settlement {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @Column(nullable = false)
//    private UUID paymentId;
//
//    private Long grossAmount;
//    private Long transferFee;
//    private Long schemeFee;
//    private Long pgFee;
//
//    @Enumerated(EnumType.STRING)
//    private SettlementStatus status;
//
//    private Instant settledAt;
//    private Instant createdAt;
//}
