package com.payment_gateway.razorpay.payment.statemachine;

import com.payment_gateway.razorpay.common.enums.PaymentActor;
import com.payment_gateway.razorpay.common.enums.PaymentEvent;
import com.payment_gateway.razorpay.common.enums.PaymentStatus;
import com.payment_gateway.razorpay.payment.entity.Payment;
import com.payment_gateway.razorpay.payment.entity.PaymentTransitionLog;
import com.payment_gateway.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent paymentEvent) {
        PaymentStatus toStatus = paymentStateMachine.transition(payment.getStatus(), paymentEvent);
        PaymentTransitionLog paymentTransitionLog = PaymentTransitionLog
                .builder()
                .event(paymentEvent)
                .fromStatus(payment.getStatus())
                .toStatus(toStatus)
                .occurredAt(Instant.now())
                .actor(PaymentActor.SYSTEM)    //TODO: Fetch merchant context to identify actor -- Spring Security
                .build();
        payment.setStatus(toStatus);    //update payment's status
        paymentTransitionLogRepository.save(paymentTransitionLog);
        return toStatus;
    }
}
