package com.payment_gateway.razorpay.common.config;

import com.payment_gateway.razorpay.common.enums.EventAggregateType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "app.kafka")
@Component
@Getter
@Setter
public class KafkaProperties {

    private Map<String, String> topics;

    public String topicFor(EventAggregateType aggregateType) {
        String topic = topics.get(aggregateType.name().toLowerCase());
        if (topic == null) {
            throw new IllegalStateException("No kafka topic is configured for aggregate type: " + aggregateType);
        }
        return topic;
    }
}
