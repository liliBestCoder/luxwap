package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * VPN线路信息对象 vpn_lines
 * 
 * @author ruoyi
 * @date 2025-08-09
 */
public class VpnLines extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 线路名称 */
    @Excel(name = "线路名称")
    private String name;

    /** 地区 */
    @Excel(name = "地区")
    private String region;

    @Excel(name = "类型")
    private String type;

    @Excel(name = "带宽")
    private Integer bandwidth;

    /** 剩余流量（字节） */
    @Excel(name = "剩余流量")
    private Long remainingTraffic;

    /** 总流量（字节） */
    @Excel(name = "总流量")
    private Long totalTraffic;

    /** 连接数 */
    @Excel(name = "连接数")
    private Integer connectionCnt;

    @Excel(name = "最大连接数")
    private Integer maxConnectionCnt;

    /** ping延迟（毫秒） */
    @Excel(name = "ping延迟")
    private Integer pingDelay;

    @Excel(name = "ping偏移")
    private Integer pingOffset;

    /** 线路服务器IP */
    @Excel(name = "线路服务器IP")
    private String ip;

    /** 线路服务器端口 */
    @Excel(name = "线路服务器端口")
    private Integer port;

    /** 搜索关键词 */
    @Excel(name = "搜索关键词")
    private String keyword;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 协议 */
    @Excel(name = "协议")
    private String protocol;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setRegion(String region) 
    {
        this.region = region;
    }

    public String getRegion() 
    {
        return region;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public void setRemainingTraffic(Long remainingTraffic)
    {
        this.remainingTraffic = remainingTraffic;
    }

    public Long getRemainingTraffic()
    {
        return remainingTraffic;
    }

    public void setTotalTraffic(Long totalTraffic)
    {
        this.totalTraffic = totalTraffic;
    }

    public Long getTotalTraffic()
    {
        return totalTraffic;
    }

    public void setConnectionCnt(Integer connectionCnt)
    {
        this.connectionCnt = connectionCnt;
    }

    public Integer getConnectionCnt()
    {
        return connectionCnt;
    }

    public Integer getMaxConnectionCnt() {
        return maxConnectionCnt;
    }

    public void setMaxConnectionCnt(Integer maxConnectionCnt) {
        this.maxConnectionCnt = maxConnectionCnt;
    }

    public void setPingDelay(Integer pingDelay)
    {
        this.pingDelay = pingDelay;
    }

    public Integer getPingDelay()
    {
        return pingDelay;
    }

    public Integer getPingOffset() {
        return pingOffset;
    }

    public void setPingOffset(Integer pingOffset) {
        this.pingOffset = pingOffset;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return ip;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setKeyword(String keyword) 
    {
        this.keyword = keyword;
    }

    public String getKeyword() 
    {
        return keyword;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setProtocol(String protocol) 
    {
        this.protocol = protocol;
    }

    public String getProtocol() 
    {
        return protocol;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }

    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("region", getRegion())
            .append("remainingTraffic", getRemainingTraffic())
            .append("totalTraffic", getTotalTraffic())
            .append("connectionCnt", getConnectionCnt())
            .append("pingDelay", getPingDelay())
            .append("ip", getIp())
            .append("port", getPort())
            .append("keyword", getKeyword())
            .append("status", getStatus())
            .append("protocol", getProtocol())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
