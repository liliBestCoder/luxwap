package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.system.domain.XrayChainProxyNode;
import com.ruoyi.system.util.ProxyCipherUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 链式代理上游端点视图对象（安全脱敏输出）
 *
 * @author ruoyi
 */
public class ChainProxyNodeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nodeKey; // 对外安全唯一业务Key（防遍历）
    private Long userId;
    private Integer sourceType;
    private String protocol;
    private String host;
    private Integer port;
    private String username;
    private String passwordMask; // 脱敏展示为 ••••••
    private String remark;
    private String exitIp;
    private String countryCode;

    private String region;

    private String city;
    private Integer aliveStatus;
    private Integer latencyMs;
    private String refreshUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCheckTime;

    private Integer sortOrder;

    public static ChainProxyNodeVO fromEntity(XrayChainProxyNode node) {
        if (node == null) return null;
        ChainProxyNodeVO vo = new ChainProxyNodeVO();
        vo.setId(node.getId());
        vo.setNodeKey(node.getNodeKey());
        vo.setUserId(node.getUserId());
        vo.setSourceType(node.getSourceType());
        vo.setProtocol(node.getProtocol());
        vo.setHost(node.getHost());
        vo.setPort(node.getPort());
        vo.setUsername(node.getUsername());
        vo.setPasswordMask(ProxyCipherUtils.mask(node.getPasswordCipher()));
        vo.setRemark(node.getRemark());
        vo.setExitIp(node.getExitIp());
        vo.setCountryCode(node.getCountryCode());
        vo.setRegion(node.getRegion());
        vo.setCity(node.getCity());
        vo.setAliveStatus(node.getAliveStatus());
        vo.setLatencyMs(node.getLatencyMs());
        vo.setRefreshUrl(node.getRefreshUrl());
        vo.setLastCheckTime(node.getLastCheckTime());
        vo.setSortOrder(node.getSortOrder());
        return vo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordMask() {
        return passwordMask;
    }

    public void setPasswordMask(String passwordMask) {
        this.passwordMask = passwordMask;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getExitIp() {
        return exitIp;
    }

    public void setExitIp(String exitIp) {
        this.exitIp = exitIp;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getAliveStatus() {
        return aliveStatus;
    }

    public void setAliveStatus(Integer aliveStatus) {
        this.aliveStatus = aliveStatus;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Integer latencyMs) {
        this.latencyMs = latencyMs;
    }

    public String getRefreshUrl() {
        return refreshUrl;
    }

    public void setRefreshUrl(String refreshUrl) {
        this.refreshUrl = refreshUrl;
    }

    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
