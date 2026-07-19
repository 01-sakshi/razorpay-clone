package com.payment_gateway.razorpay.payment.simulator;

import com.payment_gateway.razorpay.common.enums.ChaosMode;
import com.payment_gateway.razorpay.common.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "payment.simulator")
@Getter
@Setter
public class SimulatorConfig {
    //    Assign all properties with some default values
    private Integer pollIntervalMs = 2000;
    private ChaosMode chaosMode = ChaosMode.NORMAL;
    private Map<String, MethodSimulatorConfig> methods = new HashMap<>();

    //Hashmap(methods) is populated via application properties file
    MethodSimulatorConfig configOf(PaymentMethod paymentMethod) {
        return methods.getOrDefault(paymentMethod.name(), new MethodSimulatorConfig());
    }

    @Getter
    @Setter
    static class MethodSimulatorConfig {    //Doubt
        private Integer minDelaySeconds = 1;
        private Integer maxDelaySeconds = 5;
        private Integer successRate = 80;
    }
}
