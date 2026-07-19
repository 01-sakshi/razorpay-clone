package com.payment_gateway.razorpay.payment.strategy;

import com.payment_gateway.razorpay.common.util.RandomizerUtil;
import com.payment_gateway.razorpay.payment.processor.PaymentProcessor;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String VPA_CODE_FAIL = "fail@okaxis";
        String bankCode = request.methodDetails() != null ? request.methodDetails().get("vpa").toString() : null;

        //Simulation
        if (Objects.equals(bankCode, VPA_CODE_FAIL)) {
            return new PaymentProcessorResponse.Failure("UPI_REJECTED",
                    "Bank rejected the transaction");
        }

        String processorRef = "UPI_PROCESSOR_" + RandomizerUtil.randomBase64(16);
//        String bankRef = "BANK_REF" + RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
