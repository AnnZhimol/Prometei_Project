package com.example.prometei.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CipherUtil {
    public static String ALGORITHM;
    public static String TRANSFORMATION;
    public static String KEY;

    public static String encryptId(long id) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(String.valueOf(id).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypt ID", e);
        }
    }

    public static long decryptId(String encryptedId) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedId);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return Long.parseLong(new String(decryptedBytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypt ID", e);
        }
    }
}
