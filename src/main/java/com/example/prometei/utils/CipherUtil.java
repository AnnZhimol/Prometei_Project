package com.example.prometei.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

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
            return bytesToHex(encryptedBytes); // Используем Hex encoding
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при шифровании ID", e);
        }
    }

    public static long decryptId(String encryptedId) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = hexToBytes(encryptedId);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return Long.parseLong(new String(decryptedBytes, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при дешифровании ID", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
}
