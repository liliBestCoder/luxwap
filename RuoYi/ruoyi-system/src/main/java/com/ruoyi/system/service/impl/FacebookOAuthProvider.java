package com.ruoyi.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.system.domain.OAuthUserInfo;
import com.ruoyi.system.service.OAuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service("facebookOAuthProvider")
public class FacebookOAuthProvider implements OAuthProvider {

    @Value("${oauth.facebook.client-id}")
    private String clientId;
    @Value("${oauth.facebook.client-secret}")
    private String clientSecret;
    @Value("${oauth.facebook.redirect-uri}")
    private String redirectUri;

    @Override
    public String getAuthUrl(String state) {
        return "https://www.facebook.com/v12.0/dialog/oauth?client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&state=" + state
                + "&scope=email,public_profile";
    }

    @Override
    public OAuthUserInfo exchangeCodeForUser(String code) throws Exception {
        String tokenUrl = "https://graph.facebook.com/v12.0/oauth/access_token"
                + "?client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&client_secret=" + clientSecret
                + "&code=" + code;

        String tokenJson = HttpUtils.sendGet(tokenUrl);
        String accessToken = JSONObject.parseObject(tokenJson).getString("access_token");

        String userInfoJson = HttpUtils.sendGet("https://graph.facebook.com/me?fields=id,name,email,picture&access_token=" + accessToken);
        JSONObject user = JSONObject.parseObject(userInfoJson);

        return new OAuthUserInfo(
                "facebook",
                user.getString("id"),
                user.getString("email"),
                user.getString("name"),
                user.getJSONObject("picture").getJSONObject("data").getString("url")
        );
    }
}

