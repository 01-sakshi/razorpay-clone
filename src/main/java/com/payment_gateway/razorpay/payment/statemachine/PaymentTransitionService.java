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
/**
 * Applies and audits payment state-machine transitions.
 */
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    /**
     * Validates the event against the state machine, changes the in-memory payment status, and persists a system-actor
     * transition record containing the before/after states and occurrence time.
     *
     * @param payment payment entity to mutate
     * @param paymentEvent event that drives the transition
     * @return the newly assigned status
     * @throws InvalidStateTransitionException if the current state does not allow this event
     */
    public PaymentStatus apply(Payment payment, PaymentEvent paymentEvent) {
        PaymentStatus toStatus = paymentStateMachine.transition(payment.getStatus(), paymentEvent);
        PaymentTransitionLog paymentTransitionLog = PaymentTransitionLog
                .builder()
                .payment(payment)
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
