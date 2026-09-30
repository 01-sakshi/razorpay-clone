package com.payment_gateway.razorpay.payment.strategy;

import com.payment_gateway.razorpay.common.util.RandomizerUtil;
import com.payment_gateway.razorpay.payment.processor.PaymentProcessor;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "4000000000000002";
    public static final String PAN_CARD_EXPIRED = "4000000000000002";

    /** Returns a simulated decline for the configured decline PAN and a generated pending reference for other PANs. */
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        String pan = request.pan();
        if (PAN_CARD_DECLINED.equals(pan)) {
            log.error("PAN Card declined");
            return new PaymentProcessorResponse.Failure("PAN_CARD_DECLINED", "PAN Card declined");
        } else if (PAN_CARD_EXPIRED.equals(pan)) {
            log.error("PAN Card expired");
            return new PaymentProcessorResponse.Failure("PAN_CARD_EXPIRED", "PAN Card expired");
        }
        String processorReference = "CARD_PROCESSOR_" + RandomizerUtil.randomBase64(16);
        return new PaymentProcessorResponse.Pending(processorReference);
    }
}
