package com.payment_gateway.razorpay.payment.strategy;

import com.payment_gateway.razorpay.common.util.RandomizerUtil;
import com.payment_gateway.razorpay.payment.processor.PaymentProcessor;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";
        String bankCode = request.methodDetails() != null ? request.methodDetails().get("BANK").toString() : null;

        //Simulation
        if (Objects.equals(bankCode, BANK_CODE_FAIL)) {
            return new PaymentProcessorResponse.Failure("BANK_REJECTED",
                    "Bank rejected the transaction");
        }

        String processorRef = "NET_BANKING_PROCESSOR_" + RandomizerUtil.randomBase64(16);
//        String redirectRef = "https://REDIRECT_BANK.com/" + processorRef;

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
