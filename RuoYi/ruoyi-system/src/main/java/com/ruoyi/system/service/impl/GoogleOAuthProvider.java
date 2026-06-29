package com.ruoyi.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.system.domain.OAuthUserInfo;
import com.ruoyi.system.service.OAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service("googleOAuthProvider")
public class GoogleOAuthProvider implements OAuthProvider {

    @Value("${oauth.google.client-id}")
    private String clientId;
    @Value("${oauth.google.client-secret}")
    private String clientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    private static final String AUTH_BASE = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    @Override
    public String getAuthUrl(String state) {
       return AUTH_BASE + "?"
                + "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=" + URLEncoder.encode("openid email profile", StandardCharsets.UTF_8)
                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);
    }

    @Override
    public OAuthUserInfo exchangeCodeForUser(String code) throws Exception {
        // 构建 POST body
        String body = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code";

        // 1. 获取 access_token
        String tokenJson = HttpUtils.sendPost(TOKEN_URL, body);
        JSONObject tokenObj = JSONObject.parseObject(tokenJson);
        String accessToken = tokenObj.getString("access_token");

        // 2. 获取用户信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        String userJson = HttpUtils.sendGet(USERINFO_URL, headers);

        JSONObject user = JSONObject.parseObject(userJson);

        return new OAuthUserInfo(
                "google",
                user.getString("id"),
                user.getString("email"),
                user.getString("name"),
                user.getString("picture")
        );
    }
}

