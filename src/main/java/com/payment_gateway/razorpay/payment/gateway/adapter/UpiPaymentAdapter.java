package com.payment_gateway.razorpay.payment.gateway.adapter;

import com.payment_gateway.razorpay.payment.gateway.PaymentAdapter;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentRequest;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentResult;
import com.payment_gateway.razorpay.payment.processor.PaymentProcessorRouter;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpiPaymentAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {
        log.info("Initiate Payment with UpiPaymentAdapter, paymentId: {}", paymentRequest.paymentId());

        try {
            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(paymentRequest.paymentId(),
                    paymentRequest.paymentMethod(), paymentRequest.amount(), paymentRequest.methodDetails());

            //send request from PaymentAdapter to PaymentProcessor via PaymentProcessorRouter
            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);

            return switch (paymentProcessorResponse) {
                case PaymentProcessorResponse.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            };

        } catch (Exception e) {
            log.error("UPI failed, paymentId: {}", paymentRequest.paymentId());
            return new PaymentResult.Failure("UPI_FAILED", e.getMessage());  //TODO: Create constant for errorCode
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("UPI_REF");
    }
}
