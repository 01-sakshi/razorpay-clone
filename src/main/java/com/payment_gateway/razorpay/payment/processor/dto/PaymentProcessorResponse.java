package com.payment_gateway.razorpay.payment.processor.dto;

public sealed interface PaymentProcessorResponse permits PaymentProcessorResponse.Failure,
        PaymentProcessorResponse.Pending, PaymentProcessorResponse.Success {     //Doubt on parameters
    record Pending(String processorReference) implements PaymentProcessorResponse {
    }

    record Success(String processorReference, String bankReference) implements PaymentProcessorResponse {
    }

    record Failure(String errorCode, String errorDescription) implements PaymentProcessorResponse {
    }
}
