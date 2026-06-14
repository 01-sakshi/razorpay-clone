package com.payment_gateway.razorpay.common.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;


@Embeddable
@Getter
@Setter
public class Money {

    private Long amountUnits;
    private String currency;

    private Money(Long amountUnits, String currency) {
        this.amountUnits = amountUnits;
        this.currency = currency;
    }

    public Money() {

    }

    public Money of(Long amountUnits, String currency) {
        return new Money(amountUnits, currency);
    }

    public Money add(Money other) {
        if(!other.currency.equals(this.currency)) throw new IllegalArgumentException("Chal be!");
        return new Money(this.amountUnits + other.amountUnits, this.currency);
    }

    public Money subtract(Money other) {
        if(!other.currency.equals(this.currency)) throw new IllegalArgumentException("Chal be!");
        return new Money(this.amountUnits - other.amountUnits, this.currency);
    }
}
