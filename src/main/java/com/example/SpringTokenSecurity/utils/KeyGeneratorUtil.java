package com.example.SpringTokenSecurity.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class KeyGeneratorUtil {
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256); // Specify key size
        return keyGen.generateKey();
    }
}

