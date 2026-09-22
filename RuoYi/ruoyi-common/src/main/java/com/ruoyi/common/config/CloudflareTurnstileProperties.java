package com.ruoyi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Cloudflare Turnstile 人机验证与安全配置
 *
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "cloudflare.turnstile")
public class CloudflareTurnstileProperties {

    /**
     * 是否开启 Cloudflare Turnstile 人机验证防护开关
     * （未申请域名时设为 false，申请并配置后设为 true）
     */
    private boolean enabled = false;

    /**
     * 前端公钥 Site Key
     */
    private String siteKey = "";

    /**
     * 后端私钥 Secret Key
     */
    private String secretKey = "";

    /**
     * Cloudflare 校验接口地址
     */
    private String verifyUrl = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSiteKey() {
        return siteKey;
    }

    public void setSiteKey(String siteKey) {
        this.siteKey = siteKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getVerifyUrl() {
        return verifyUrl;
    }

    public void setVerifyUrl(String verifyUrl) {
        this.verifyUrl = verifyUrl;
    }
}
