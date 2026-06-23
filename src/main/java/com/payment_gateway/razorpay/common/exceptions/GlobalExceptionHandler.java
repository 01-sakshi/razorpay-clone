package com.payment_gateway.razorpay.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of(e.getErrorCode(), e.getMessage())
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        String errorCode = e.getResource().toUpperCase() + "_NOT_FOUND";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponse.of(errorCode, e.getMessage())
        );
    }

    @ExceptionHandler(ApiKeyDisabledException.class)
    public ResponseEntity<ErrorResponse> handleApiKeyDisabledException(ApiKeyDisabledException e) {
        String errorCode = e.getResource().toUpperCase() + "_DISABLED";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of(errorCode, e.getMessage(), (String) e.getIdentifier(), null)
        );
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolationException(BusinessRuleViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponse.of(e.getErrorCode(), e.getMessage(), (String) e.getIdentifier(), null)
        );
    }
}
