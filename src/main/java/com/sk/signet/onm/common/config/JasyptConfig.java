package com.sk.signet.onm.common.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class JasyptConfig {

    @Value("${jasypt.encryptor.password}")
    private String encryptKey;
    
    @Bean("encryptorBean")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setProviderName("SunJCE");
        encryptor.setPoolSize(2);
        encryptor.setPassword(encryptKey);
        encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
        return encryptor;
    }
    
}
