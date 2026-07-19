package com.payment_gateway.razorpay.vault.serviceImpl.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class ExpiryYearValidator implements ConstraintValidator<ExpiryYear, Integer> {
    @Override
    public boolean isValid(Integer expiryYear, ConstraintValidatorContext context) {
        if (expiryYear == null) return false;
        Year now = Year.now();
        return now.getValue() <= expiryYear;
    }
}
