package com.payment_gateway.razorpay.payment.simulator;

import com.payment_gateway.razorpay.common.enums.ChaosMode;
import com.payment_gateway.razorpay.common.enums.PaymentStatus;
import com.payment_gateway.razorpay.common.util.RandomizerUtil;
import com.payment_gateway.razorpay.payment.entity.Payment;
import com.payment_gateway.razorpay.payment.repository.PaymentRepository;
import com.payment_gateway.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BankCallbackSimulator {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallbacks() {
        Instant globalWindow = Instant.now().minusSeconds(1);   //Take all payments in AUTHORIZING status till (current time - 1 second)
        List<Payment> candidates = paymentRepository.findByStatusAndUpdatedAtBefore(PaymentStatus.AUTHORIZING,
                globalWindow); //Doubt: CreatedAt or UpdatedAt?

        if (candidates.isEmpty()) return;
        for (Payment payment : candidates) {
            simulateCallback(payment);
        }
    }

    private void simulateCallback(Payment payment) {
        SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig = simulatorConfig.configOf(payment.getMethod());

        Instant isDue = dueAt(payment, methodSimulatorConfig);
        if (Instant.now().isBefore(isDue)) return;

        switch (simulatorConfig.getChaosMode()) {
            case ChaosMode.FAILURE -> resolve(payment, false);
            case ChaosMode.SUCCESS -> resolve(payment, true);
            case ChaosMode.NORMAL, SLOW -> resolve(payment, shouldApprove(payment, methodSimulatorConfig));
            case ChaosMode.TIMEOUT -> {
                log.error("Payment timed out, payment id: {}", payment.getId());
            }
        }
    }

    private void resolve(Payment payment, boolean approve) {
        String bank_ref = "SIM_BANK_REF" + RandomizerUtil.randomBase64(8);
        if (approve) {
            paymentService.resolveAuthorization(payment.getId(), true, bank_ref, null, null);
        } else {
            paymentService.resolveAuthorization(payment.getId(), false, bank_ref, "SIM_BANK_ERROR_CODE",
                    "Simulated bank declined");
        }
    }

    private boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return methodSimulatorConfig.getSuccessRate() > bucket; //success scenario
    }

    private Instant dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig) {
        int range = methodSimulatorConfig.getMaxDelaySeconds() - methodSimulatorConfig.getMinDelaySeconds();
        int delaySeconds = methodSimulatorConfig.getMinDelaySeconds() + (Math.abs(payment.getId().hashCode()) % (range + 1));

        if (simulatorConfig.getChaosMode().equals(ChaosMode.SLOW)) delaySeconds *= 2;
        return payment.getUpdatedAt().plusSeconds(delaySeconds);    //or CreatedAt?
    }
}
