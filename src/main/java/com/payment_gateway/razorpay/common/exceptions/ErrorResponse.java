package com.payment_gateway.razorpay.common.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String errorCode,
        String errorDescription,
        Instant timeStamp,
        List<FieldError> fieldErrors
) {
    public record FieldError(
            String field,
            String message
    ) {
    }

    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message, Instant.now(), null);
    }

    public static ErrorResponse of(String errorCode, String message, List<FieldError> fieldErrors) {
        return new ErrorResponse(errorCode, message, Instant.now(), fieldErrors);
    }
}
