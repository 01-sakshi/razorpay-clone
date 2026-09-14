package com.payment_gateway.razorpay.common.config;

import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

@Configuration 
public class AesEncryptionConfig {

    @Value("${vault.service.master-key}")
    private String masterKey;
    
    @Bean 
    public BytesEncryptor masterKeyEncryptor() {
        byte[] masterKeyBytes = Base64.getDecoder().decode(masterKey);
        SecretKeySpec decKey = new SecretKeySpec(masterKeyBytes, "AES");   //Use masterKeyBytes to encrypt using AES algo
        return new AesBytesEncryptor(decKey,
                KeyGenerators.secureRandom(12), AesBytesEncryptor.CipherAlgorithm.GCM);
    }
}
