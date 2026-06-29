package com.ruoyi.system.service;

import com.ruoyi.system.domain.OAuthUserInfo;

public interface OAuthProvider {
    String getAuthUrl(String state);
    OAuthUserInfo exchangeCodeForUser(String code) throws Exception;
}

