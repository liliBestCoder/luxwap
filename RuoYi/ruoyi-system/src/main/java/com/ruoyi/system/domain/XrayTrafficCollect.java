package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 流量数据上报对象 xray_traffic_collect
 * 
 * @author ruoyi
 * @date 2025-08-08
 */
public class XrayTrafficCollect extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 客户端ip */
    @Excel(name = "客户端ip")
    private String clientIp;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 级别 user/inbounds */
    @Excel(name = "级别 user/inbounds")
    private String level;

    /** 类型 up上传流量/down下载流量 */
    @Excel(name = "类型 up上传流量/down下载流量")
    private String type;

    /** 值 */
    @Excel(name = "值")
    private Long val;

    /** 上报时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "上报时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date time;

    /** 状态 0未被统计 1已被统计 */
    @Excel(name = "状态 0未被统计 1已被统计")
    private Long status;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setClientIp(String clientIp) 
    {
        this.clientIp = clientIp;
    }

    public String getClientIp() 
    {
        return clientIp;
    }

    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }

    public void setLevel(String level) 
    {
        this.level = level;
    }

    public String getLevel() 
    {
        return level;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setVal(Long val) 
    {
        this.val = val;
    }

    public Long getVal() 
    {
        return val;
    }

    public void setTime(Date time) 
    {
        this.time = time;
    }

    public Date getTime() 
    {
        return time;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
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
            .append("clientIp", getClientIp())
            .append("email", getEmail())
            .append("level", getLevel())
            .append("type", getType())
            .append("val", getVal())
            .append("time", getTime())
            .append("status", getStatus())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
