package com.payment_gateway.razorpay.merchant.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String email, UUID merchantId, String role) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(Instant.now()))
                .claim("merchant_id", merchantId)
                .claim("role", role)
                .expiration(Date.from(Instant.now().plusSeconds(60 * 100)))
                .signWith(getSecretKey())
                .compact();
    }

    //Only for valid JWT Token, Claims is returned from here
    public Claims verifyAccessToken(String accessToken) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(accessToken)
                .getPayload();
    }

}
