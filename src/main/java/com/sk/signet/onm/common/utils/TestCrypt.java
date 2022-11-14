package com.sk.signet.onm.common.utils;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;

public class TestCrypt {

    
    public static void main(String[] args) {
        
        PooledPBEStringEncryptor pbeEnc = new PooledPBEStringEncryptor();
        pbeEnc.setProviderName("SunJCE");
        pbeEnc.setPoolSize(2);
        pbeEnc.setAlgorithm("PBEWithMD5AndTripleDES");
        pbeEnc.setPassword("sksignet_onm_jasypt");

        String enc1 = pbeEnc.encrypt("jdbc:mariadb://10.10.10.163:3306/signetCpos?useSSL=false&amp;serverTimezone=UTC&amp;autoReconnection=true");
        String enc2 = pbeEnc.encrypt("cpos");
        String enc3 = pbeEnc.encrypt("cpos!@34");
        System.out.println("enc1 = " + enc1);
        System.out.println("enc2 = " + enc2);
        System.out.println("enc3 = " + enc3);

        String des = pbeEnc.decrypt(enc1);
        System.out.println("des = " + des);
    }

}
