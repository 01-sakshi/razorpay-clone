package com.payment_gateway.razorpay.vault.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class VaultServiceConfig {

    @Value("${vault.service.master-key}")
    private String masterKey;


    public static BytesEncryptor panEncrypter(byte[] dek) {
        SecretKeySpec decKey = new SecretKeySpec(dek, "AES");   //Use dek to encrypt by using AES algo
        return new AesBytesEncryptor(decKey,
                KeyGenerators.secureRandom(12), AesBytesEncryptor.CipherAlgorithm.GCM);

    }

    @Bean
    public BytesEncryptor decEncryptor() {
        byte[] masterKeyBytes = Base64.getDecoder().decode(masterKey);
        return panEncrypter(masterKeyBytes);
    }
}
