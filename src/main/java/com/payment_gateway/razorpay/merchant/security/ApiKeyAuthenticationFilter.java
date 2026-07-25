package com.payment_gateway.razorpay.merchant.security;

import com.payment_gateway.razorpay.merchant.entity.ApiKey;
import com.payment_gateway.razorpay.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final String BASIC_PREFIX = "Basic ";
    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();   //To avoid circular dependency with WebSecurityConfig respect to password encoder
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            final String authorizationHeader = request.getHeader("Authorization");

            /* We didn't receive api key as part of authorizationHeader in the API */
            if (authorizationHeader == null || !authorizationHeader.startsWith(BASIC_PREFIX)) {
                /* Pass the request to the next security filter since this filter is not applicable for the request */
                filterChain.doFilter(request, response);
                return;
            }

            String[] decoded = decode(authorizationHeader);
            if (decoded == null) return;
            String secretKey = decoded[1];
            String keyId = decoded[0];

            ApiKey apiKey = apiKeyRepository.findByKeyId(keyId).orElseThrow(
                    () -> new BadRequestException("Invalid or missing API Key"));

            if (!apiKey.getEnabled() || !secretMatches(secretKey, apiKey))
                throw new BadRequestException("Invalid or missing API Key");

            var auth = new UsernamePasswordAuthenticationToken(keyId, null,
                    List.of(new SimpleGrantedAuthority("AUTH_KEY_ROLE")));

            SecurityContextHolder.getContext().setAuthentication(auth);
            merchantContext.setMerchantId(apiKey.getMerchant().getId());
            merchantContext.setKeyId(keyId);

            /* Pass the request to the next security filter */
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            /* When we do not specify the handler below and pass it as null, the exception gets propagated
            to GlobalExceptionHandler which in normal cases only handles the exceptions of MVC layer. */
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }

    private boolean secretMatches(String rawSecret, ApiKey apiKey) {
        if (BCRYPT.matches(rawSecret, apiKey.getKeySecretHash()))
            return true;   /* Verify the encoded password obtained from storage matches the submitted raw
         password after it too is encoded. */
        return apiKey.getGracePeriodExpiresAt() != null &&
                Instant.now().isBefore(apiKey.getGracePeriodExpiresAt()) &&
                apiKey.getPreviousKeySecretHash() != null &&
                apiKey.getPreviousKeySecretHash().equals(apiKey.getKeyId());
    }

    private String[] decode(String header) {
        String encoded = header.substring(BASIC_PREFIX.length());
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        int colon = decoded.indexOf(':');
        if (colon < 0) return null;
        return new String[]{decoded.substring(0, colon), decoded.substring(colon + 1)};
    }
}
