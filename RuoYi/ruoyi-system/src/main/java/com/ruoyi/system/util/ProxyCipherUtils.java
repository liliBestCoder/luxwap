package com.ruoyi.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 链式代理高敏密码 AES-256-GCM 安全加密/解密工具
 *
 * @author ruoyi
 */
public class ProxyCipherUtils {

    private static final Logger log = LoggerFactory.getLogger(ProxyCipherUtils.class);

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;

    // 系统内部种子密钥（可由环境变量或配置中心替换）
    private static final String DEFAULT_SALT = "Luxwap_Proxy_Chain_Secret_Key_2026";

    private static SecretKey deriveKey(String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(salt.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("初始化加密密钥失败", e);
        }
    }

    /**
     * 加密明文密码为 Base64 密文（含随机 IV）
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return "";
        }
        try {
            SecretKey key = deriveKey(DEFAULT_SALT);
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 将 IV 与密文拼接: [12 bytes IV] + [Cipher bytes]
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            log.error("代理密码加密失败: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 解密 Base64 密文为明文密码
     */
    public static String decrypt(String cipherTextBase64) {
        if (cipherTextBase64 == null || cipherTextBase64.isEmpty()) {
            return "";
        }
        try {
            byte[] combined = Base64.getDecoder().decode(cipherTextBase64);
            if (combined.length <= GCM_IV_LENGTH) {
                return "";
            }

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);

            int cipherLength = combined.length - GCM_IV_LENGTH;
            byte[] cipherText = new byte[cipherLength];
            System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherLength);

            SecretKey key = deriveKey(DEFAULT_SALT);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] plainBytes = cipher.doFinal(cipherText);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("代理密码解密失败，返回空: {}", e.getMessage());
            return "";
        }
    }

    /**
     * 密码掩码脱敏
     */
    public static String mask(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        return "••••••";
    }
}
