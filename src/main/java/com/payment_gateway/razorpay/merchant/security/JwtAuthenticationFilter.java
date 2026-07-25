package com.payment_gateway.razorpay.merchant.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final MerchantContext merchantContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            final String authorizationHeader = request.getHeader("Authorization");

            /* We didn't receive JWT token as part of authorizationHeader in the API */
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
                /* Pass the request to the next security filter since this filter is not applicable for the request */
                filterChain.doFilter(request, response);
                return;
            }

            String jwtToken = authorizationHeader.substring("Bearer ".length());

            /* Verify the JWT token received above via authorizationHeader */
            Claims claims = jwtUtil.verifyAccessToken(jwtToken);

            /* Set the security context wth the current user's authentication object if it's not already done */
            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + jwtUtil.extractRole(claims))));
                /* Set the security context wth the current user's authentication object */
                SecurityContextHolder.getContext().setAuthentication(auth);

                merchantContext.setMerchantId(UUID.fromString(jwtUtil.extractMerchantId(claims)));
                IO.println("merchant id in merchantContext: " + merchantContext.getMerchantId());
            }

            /* Pass the request to the next security filter */
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            /* When we do not specify the handler below and pass it as null, the exception gets propagated
            to GlobalExceptionHandler which in normal cases only handles the exceptions of MVC layer. */
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
