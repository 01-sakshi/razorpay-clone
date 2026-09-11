package com.payment_gateway.razorpay.vault.serviceImpl;

import com.payment_gateway.razorpay.common.entity.Money;
import com.payment_gateway.razorpay.common.enums.CardBrand;
import com.payment_gateway.razorpay.common.exceptions.ResourceNotFoundException;
import com.payment_gateway.razorpay.common.util.RandomizerUtil;
import com.payment_gateway.razorpay.payment.processor.PaymentProcessorRouter;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.payment_gateway.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.payment_gateway.razorpay.vault.config.VaultServiceConfig;
import com.payment_gateway.razorpay.vault.dto.request.TokenizeRequest;
import com.payment_gateway.razorpay.vault.dto.response.TokenizeResponse;
import com.payment_gateway.razorpay.vault.entity.CardToken;
import com.payment_gateway.razorpay.vault.entity.VaultCard;
import com.payment_gateway.razorpay.vault.repository.CardTokenRepository;
import com.payment_gateway.razorpay.vault.repository.VaultCardRepository;
import com.payment_gateway.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaultServiceImpl implements VaultService {

    private final CardTokenRepository cardTokenRepository;
    private final VaultCardRepository vaultCardRepository;
    private final BytesEncryptor decEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    @Transactional
    public TokenizeResponse tokenize(TokenizeRequest tokenizeRequest, UUID merchantId) {
        String lastFour = tokenizeRequest.pan().substring(tokenizeRequest.pan().length() - 4);
        String bin = tokenizeRequest.pan().substring(0, 6); //First six digits of PAN
        CardBrand cardBrand = detectBrand(bin);
        byte[] dek = KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan = VaultServiceConfig.panEncrypter(dek)
                .encrypt(tokenizeRequest.pan().getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDek = decEncryptor.encrypt(dek);

        VaultCard vaultCard = VaultCard
                .builder()
                .bin(bin)
                .lastFour(lastFour)
                .brand(cardBrand)
                .expiryMonth(tokenizeRequest.expiryMonth().toString())
                .expiryYear(tokenizeRequest.expiryYear().toString())
                .encryptedPan(encryptedPan)
                .encryptedDek(encryptedDek)
                .cardHolderName(tokenizeRequest.cardHolderName())
                .build();

        String token = "tok_" + RandomizerUtil.randomBase64(32);

        CardToken cardToken = CardToken
                .builder()
                .token(token)
                .merchantId(merchantId)
                .customerId(tokenizeRequest.customerId())
                .vaultCard(vaultCard)
                .build();

        vaultCard = vaultCardRepository.save(vaultCard);
        cardToken = cardTokenRepository.save(cardToken);
        return new TokenizeResponse(
                token,
                tokenizeRequest.expiryMonth(),
                tokenizeRequest.expiryYear(),
                cardBrand,
                lastFour);
    }

    @Override
    @Transactional
    public PaymentProcessorResponse charge(UUID paymentId, String token, Money amount, Map<String, Object> methodDetails) {
        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtNull(token)
                .orElseThrow(() -> new ResourceNotFoundException("CardToken", token));

        VaultCard vaultCard = cardToken.getVaultCard();
        byte[] decryptedPanBytes = null;

        try {
            byte[] encryptedDek = vaultCard.getEncryptedDek();
            byte[] decryptedDek = decEncryptor.decrypt(encryptedDek);
            decryptedPanBytes = VaultServiceConfig.panEncrypter(decryptedDek).decrypt(vaultCard.getEncryptedDek());
            String pan = new String(decryptedPanBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.card(paymentId, pan, expiry,
                    amount, methodDetails);

            PaymentProcessorResponse paymentProcessorResponse = paymentProcessorRouter.charge(paymentProcessorRequest);

            log.info("Vault charge registered, token={}****", token.substring(0, 4));
            return paymentProcessorResponse;
        } catch (Exception e) {
            log.error("Vault charge registered, token={}****", token.substring(0, 4));
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        } finally {
            if (decryptedPanBytes != null) Arrays.fill(decryptedPanBytes, (byte) 0);
        }
    }

    private CardBrand detectBrand(String ch) {
        if (ch.startsWith("4")) return CardBrand.VISA;
        else if (ch.startsWith("5") || ch.startsWith("2")) return CardBrand.MASTERCARD;
        else if (ch.startsWith("37") || ch.startsWith("34")) return CardBrand.AMEX;
        return CardBrand.RUPAY;
    }
}
