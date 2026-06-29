package com.ruoyi.system.domain;

public class OAuthUserInfo {
    private String provider;
    private String providerUserId;
    private String email;
    private String name;
    private String avatarUrl;

    // OAuthUserInfo.java 中添加以下构造函数
    public OAuthUserInfo(String provider, String id, String email, String name, String profileImageUrl) {
        this.provider = provider;
        this.providerUserId = id;
        this.email = email;
        this.name = name;
        this.avatarUrl = profileImageUrl;
    }


    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public void setProviderUserId(String providerUserId) {
        this.providerUserId = providerUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
