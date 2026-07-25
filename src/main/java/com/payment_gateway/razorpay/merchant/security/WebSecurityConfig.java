package com.payment_gateway.razorpay.merchant.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    private static final String[] JWT_ROUTES = {"/v1/actuator/**", "/v1/merchant/**", "/v1/admin/**"};  // /v1/auth/** routes??
    private static final String[] API_KEY_ROUTES = {"/v1/orders/**", "/v1/payments/**", "/v1/vault/**"};

    @Bean
    public SecurityFilterChain jwtChain(HttpSecurity httpSecurity) {
        return httpSecurity
                .securityMatcher(JWT_ROUTES)      /* Only the routes that are allowed as per JWT_ROUTES will be permitted here and the following code will apply to them only */
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/auth/signup", "/v1/auth/login").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formLogin -> formLogin.disable())
                .build();
    }

    @Bean
    public PasswordEncoder encodedPassword() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(MerchantUserDetailsService merchantUserDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(merchantUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }
}
