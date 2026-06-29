package com.ruoyi.system.domain;

public class OAuthTaskResult {
    private String status; // pending / success
    private String token;
    private String msg;

    public OAuthTaskResult(String status, String token, String msg) {
        this.status = status;
        this.token = token;
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
