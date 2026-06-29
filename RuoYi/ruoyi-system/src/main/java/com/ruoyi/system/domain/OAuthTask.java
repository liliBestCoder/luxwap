package com.ruoyi.system.domain;

import java.time.LocalDateTime;

public class OAuthTask {
    private String taskId;
    private OAuthTaskRequest request;
    private String status = "pending";
    private String token;
    private String msg;
    private String countryCode;
    private LocalDateTime createTime;

    private static final int EXPIRE_MINUTES = 5; // 过期时间：5分钟

    public OAuthTask(String taskId, String countryCode, OAuthTaskRequest request) {
        this.taskId = taskId;
        this.request = request;
        this.countryCode = countryCode;
        this.createTime = LocalDateTime.now();
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public OAuthTaskRequest getRequest() {
        return request;
    }

    public void setRequest(OAuthTaskRequest request) {
        this.request = request;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isExpired() {
        // 如果当前时间减去创建时间超过 5 分钟，则认为任务过期
        return LocalDateTime.now().isAfter(createTime.plusMinutes(EXPIRE_MINUTES));
    }

}
