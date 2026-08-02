package com.payment_gateway.razorpay.vault.controller;

import com.payment_gateway.razorpay.merchant.security.MerchantContext;
import com.payment_gateway.razorpay.vault.dto.request.TokenizeRequest;
import com.payment_gateway.razorpay.vault.dto.response.TokenizeResponse;
import com.payment_gateway.razorpay.vault.service.VaultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vault")
public class VaultController {

    private final VaultService vaultService;
    private MerchantContext merchantContext;

    @PostMapping("/tokenize")
    public ResponseEntity<TokenizeResponse> tokenize(@RequestBody @Valid TokenizeRequest tokenizeRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(
                vaultService.tokenize(tokenizeRequest, merchantContext.getMerchantId())
        );
    }
}
