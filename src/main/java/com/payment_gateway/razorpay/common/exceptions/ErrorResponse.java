package com.payment_gateway.razorpay.common.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String errorCode,
        String errorDescription,
        String identifier,
        List<FieldError> fieldErrors,
        Instant timeStamp
) {
    public record FieldError(
            String field,
            String message
    ) {
    }

    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message, null, null, Instant.now());
    }

    public static ErrorResponse of(String errorCode, String message, List<FieldError> fieldErrors) {
        return new ErrorResponse(errorCode, message,null, fieldErrors, Instant.now());
    }

    public static ErrorResponse of(String errorCode, String message, String identifier, List<FieldError> fieldErrors) {
        return new ErrorResponse(errorCode, message, identifier, fieldErrors, Instant.now());
    }
}
