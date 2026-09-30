package com.payment_gateway.razorpay.payment.config;

import com.payment_gateway.razorpay.common.enums.PaymentMethod;
import com.payment_gateway.razorpay.payment.gateway.PaymentAdapter;
import com.payment_gateway.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.payment_gateway.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.payment_gateway.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final CardPaymentAdapter cardPaymentAdapter;
    private final NetBankingAdapter netBankingAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;

    /** Builds the immutable method-to-adapter registry used by the gateway router. */
    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap() {
        return Map.of(
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.UPI, upiPaymentAdapter,
                PaymentMethod.NETBANKING, netBankingAdapter
        );
    }
}
