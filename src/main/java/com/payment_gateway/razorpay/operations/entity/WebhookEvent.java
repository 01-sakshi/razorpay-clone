package com.payment_gateway.razorpay.operations.entity;

import com.payment_gateway.razorpay.common.enums.WebhookEventStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "webhook-event"
)
@Getter
@Setter
public class WebhookEvent {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID merchantId;

    @Column(nullable = false)
    private String eventType;

    @Enumerated
    private WebhookEventStatus eventStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(nullable = false)
    private Integer attempts = 0;

    private String signature;

    private String targetUrl;

    /* Doubt
        private Instant nextRetryAt;

        private Instant lastAttemptAt;

        private Integer lastResponseCode;

        @Column(length = 1000)
        private String lastResponseBody;
    */
    private Instant deliveredAt;
    private Instant createdAt;


}
