package com.payment_gateway.razorpay.payment.gateway.adapter;

import com.payment_gateway.razorpay.payment.gateway.PaymentAdapter;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentRequest;
import com.payment_gateway.razorpay.payment.gateway.dto.PaymentResult;
import com.payment_gateway.razorpay.payment.processor.PaymentProcessorRouter;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.payment_gateway.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardPaymentAdapter implements PaymentAdapter {

    private final PaymentProcessorRouter paymentProcessorRouter;
    private final VaultService vaultService;

    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {
        log.info("Initiate Payment with CardPaymentAdapter, paymentId: {}", paymentRequest.paymentId());
        try {
            String token = (String) paymentRequest.methodDetails().get("token");
            PaymentProcessorResponse paymentProcessorResponse = vaultService.charge(paymentRequest.paymentId(), token,
                    paymentRequest.amount(), paymentRequest.methodDetails());

            return switch (paymentProcessorResponse) {
                case PaymentProcessorResponse.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            };
        } catch (Exception e) {
            log.error("CardPayment failed, paymentId: {}", paymentRequest.paymentId());
            return new PaymentResult.Failure("CARD_PAYMENT_FAILED", e.getMessage());  //TODO: Create constant for errorCode
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");
    }
}
