package com.payment_gateway.razorpay.common.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessRuleViolationException extends RuntimeException {
    private final String errorCode;
    private final String resource;
    private final Object identifier;

    public BusinessRuleViolationException(String errorCode, String resource, Object identifier) {
        super("Order with orderId: " + identifier + " is already cancelled");
        this.errorCode = errorCode;
        this.resource = resource;
        this.identifier = identifier;
    }

}
