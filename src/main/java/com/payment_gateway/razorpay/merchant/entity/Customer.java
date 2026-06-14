package com.payment_gateway.razorpay.merchant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;      //Doubt

    private String name;

    @Column(length = 50)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 20)
    private String gstId;

    private Instant createdAt;
    private Instant updatedAt;
}
