package com.ruoyi.framework.security;

import com.ruoyi.common.config.CloudflareTurnstileProperties;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Cloudflare Turnstile 人机验证服务
 *
 * @author ruoyi
 */
@Service
public class CloudflareTurnstileService {

    private static final Logger log = LoggerFactory.getLogger(CloudflareTurnstileService.class);

    @Autowired
    private CloudflareTurnstileProperties properties;

    /**
     * 是否启用了人机验证防护
     */
    public boolean isEnabled() {
        return properties != null && properties.isEnabled();
    }

    /**
     * 获取前端展示用的 Site Key
     */
    public String getSiteKey() {
        return properties != null ? properties.getSiteKey() : "";
    }

    /**
     * 校验 Cloudflare 验证令牌
     *
     * @param token    前端生成的 cf-turnstile-response
     * @param remoteIp 客户端真实 IP
     * @return 验证是否通过
     */
    public boolean verify(String token, String remoteIp) {
        // 未开启时直接放行，不阻断正常登录
        if (!isEnabled()) {
            return true;
        }

        if (StringUtils.isBlank(token)) {
            log.warn("Cloudflare Turnstile 验证失败: token 为空");
            return false;
        }

        try {
            String postData = "secret=" + URLEncoder.encode(properties.getSecretKey(), StandardCharsets.UTF_8.name())
                    + "&response=" + URLEncoder.encode(token, StandardCharsets.UTF_8.name());
            if (StringUtils.isNotBlank(remoteIp)) {
                postData += "&remoteip=" + URLEncoder.encode(remoteIp, StandardCharsets.UTF_8.name());
            }

            URL url = new URL(properties.getVerifyUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(postData.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                log.error("Cloudflare Turnstile API 响应非 200: {}", responseCode);
                return false;
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            String responseBody = sb.toString();
            log.info("Cloudflare Turnstile 校验响应: {}", responseBody);

            // 解析返回体，检查 "success": true
            return responseBody.contains("\"success\":true") || responseBody.contains("\"success\": true");
        } catch (Exception e) {
            log.error("Cloudflare Turnstile 验证发生异常", e);
            // 异常时如果配置了严格模式可返回 false
            return false;
        }
    }
}
