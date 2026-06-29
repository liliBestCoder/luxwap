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

@Service("xOAuthProvider")
public class XOAuthProvider implements OAuthProvider {

    @Value("${oauth.x.client-id}")
    private String clientId;
    @Value("${oauth.x.client-secret}")
    private String clientSecret;
    @Value("${oauth.x.redirect-uri}")
    private String redirectUri;

    @Override
    public String getAuthUrl(String state) {
        return "https://twitter.com/i/oauth2/authorize?response_type=code"
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=" + URLEncoder.encode("tweet.read users.read offline.access", StandardCharsets.UTF_8)
                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                + "&code_challenge=challenge"
                + "&code_challenge_method=plain";
    }

    @Override
    public OAuthUserInfo exchangeCodeForUser(String code) throws Exception {
        String body = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&grant_type=authorization_code"
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&code_verifier=challenge";

        String tokenJson = HttpUtils.sendPost("https://api.x.com/2/oauth2/token", body);
        JSONObject tokenObj = JSONObject.parseObject(tokenJson);
        String accessToken = tokenObj.getString("access_token");

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        String userJson = HttpUtils.sendGet("https://api.x.com/2/users/me?user.fields=profile_image_url", headers);
        JSONObject userRoot = JSONObject.parseObject(userJson);
        JSONObject user = userRoot.getJSONObject("data");

        return new OAuthUserInfo(
                "x",
                user.getString("id"),
                user.getString("username"),
                user.getString("username"),
                user.getString("profile_image_url")
        );
    }
}

