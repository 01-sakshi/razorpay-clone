package com.payment_gateway.razorpay.common.audit;

import com.payment_gateway.razorpay.merchant.security.MerchantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAwareImpl")
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final MerchantContext merchantContext;

    /**
     * Selects the API key ID, then the merchant ID, as the audit principal for the current request.
     * Returns empty when neither identity is available or the request context cannot be read.
     *
     * @return the audit principal to persist, or empty when no authenticated identity is present
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String keyId = merchantContext.getKeyId();
            if (keyId != null && !keyId.isBlank()) return Optional.of(keyId);
            if (merchantContext.getMerchantId() != null) {
                return Optional.of("merchant_id: " + merchantContext.getMerchantId());
            }
        } catch (Exception ignored) {

        }
        return Optional.of("SYSTEM");
    }
}
