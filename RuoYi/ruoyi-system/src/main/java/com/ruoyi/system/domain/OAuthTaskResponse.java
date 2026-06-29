package com.ruoyi.system.domain;

public class OAuthTaskResponse {
    private String taskId;
    private String authUrl;

    public OAuthTaskResponse(String taskId, String authUrl) {
        this.taskId = taskId;
        this.authUrl = authUrl;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }
}
