package com.ruoyi.system.domain.dto;

import java.io.Serializable;

/**
 * 客户端本地测活与延迟探测结果上报 DTO
 * 由客户端本地网络环境真实测量后回传服务端同步
 *
 * @author ruoyi
 */
public class ChainProxyStatusReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 对外业务安全Key */
    private String nodeKey;

    /** 客户端本地测出的存活状态：1-可用，2-不可达 */
    private Integer aliveStatus;

    /** 客户端本地到跳板的真实网络延迟（毫秒） */
    private Integer latencyMs;

    /** 客户端探测获取的出口IP（可选） */
    private String exitIp;

    /** 客户端探测获取的国家代码（可选） */
    private String countryCode;

    public String getNodeKey() {
        return nodeKey;
    }

    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
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
}
