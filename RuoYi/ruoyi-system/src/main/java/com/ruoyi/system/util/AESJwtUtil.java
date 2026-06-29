package com.ruoyi.system.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AESJwtUtil {

    // 生成 AES key（8字节userId填充到16字节）
    public static SecretKey generateKey(long userId) {
        byte[] keyBytes = new byte[16];
        ByteBuffer.wrap(keyBytes).putLong(userId); // 高8字节是userId，低8字节0
        return new SecretKeySpec(keyBytes, "AES");
    }

    // 加密
    public static String encryptUuid(String uuid, long userId, long expirationMillis) throws Exception {
        String payload = "{ \"uuid\": \"" + uuid + "\", \"exp\": " + (System.currentTimeMillis() + expirationMillis) + " }";
        SecretKey key = generateKey(userId);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encrypted = cipher.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        byte[] tokenBytes = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, tokenBytes, 0, iv.length);
        System.arraycopy(encrypted, 0, tokenBytes, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(tokenBytes);
    }

    // 解密
    public static String decryptUuid(String token, long userId) throws Exception {
        byte[] tokenBytes = Base64.getDecoder().decode(token);
        byte[] iv = new byte[12];
        byte[] encrypted = new byte[tokenBytes.length - 12];
        System.arraycopy(tokenBytes, 0, iv, 0, 12);
        System.arraycopy(tokenBytes, 12, encrypted, 0, encrypted.length);

        SecretKey key = generateKey(userId);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }
}

