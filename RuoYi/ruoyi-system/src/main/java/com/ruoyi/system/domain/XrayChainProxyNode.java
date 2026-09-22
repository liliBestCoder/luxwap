package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 链式代理上游端点实体对象 xray_chain_proxy_node
 *
 * @author ruoyi
 */
public class XrayChainProxyNode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 对外业务安全唯一Key（防遍历） */
    private String nodeKey;

    /** 所属用户ID（0表示公共平台池，>0表示个人私有端点） */
    private Long userId;

    /** 端点来源：1-用户自建导入，2-平台统一分发 */
    private Integer sourceType;

    /** 代理协议：socks5, http, https */
    private String protocol;

    /** 代理主机（IPv4、IPv6或域名） */
    private String host;

    /** 代理端口（1-65535） */
    private Integer port;

    /** 认证账号 */
    private String username;

    /** 认证密码（AES密文） */
    private String passwordCipher;

    /** 用户自定义备注 */
    private String remark;

    /** 实际出网探测IP */
    private String exitIp;

    /** 出口国家代码（如：US, HK, JP, TW） */
    private String countryCode;

    /** 出口省份/州 */
    private String region;

    /** 出口城市 */
    private String city;

    /** 存活状态：0-未检测，1-可用有效，2-不可达/异常 */
    private Integer aliveStatus;

    /** 探测延迟（毫秒） */
    private Integer latencyMs;

    /** 动态住宅代理刷新提取URL */
    private String refreshUrl;

    /** 最近一次查活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCheckTime;

    /** 排序权重 */
    private Integer sortOrder;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

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

    public String getPasswordCipher() {
        return passwordCipher;
    }

    public void setPasswordCipher(String passwordCipher) {
        this.passwordCipher = passwordCipher;
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

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
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
