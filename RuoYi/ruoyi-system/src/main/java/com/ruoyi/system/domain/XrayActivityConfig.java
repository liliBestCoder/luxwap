package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 xray_activity_config
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
public class XrayActivityConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增ID */
    private Long id;

    /** 参与人数 */
    @Excel(name = "参与人数")
    private Long participantsCnt;

    /** 调节人数 */
    @Excel(name = "调节人数")
    private Long moderatorsCnt;

    /** 活动状态：0-终止，1-开始 */
    @Excel(name = "活动状态：0-终止，1-开始")
    private Long status;

    /** 创建时间，默认当前时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "创建时间，默认当前时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 修改时间，默认当前时间，更新时自动更改 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "修改时间，默认当前时间，更新时自动更改", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setParticipantsCnt(Long participantsCnt) 
    {
        this.participantsCnt = participantsCnt;
    }

    public Long getParticipantsCnt() 
    {
        return participantsCnt;
    }

    public void setModeratorsCnt(Long moderatorsCnt) 
    {
        this.moderatorsCnt = moderatorsCnt;
    }

    public Long getModeratorsCnt() 
    {
        return moderatorsCnt;
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
            .append("participantsCnt", getParticipantsCnt())
            .append("moderatorsCnt", getModeratorsCnt())
            .append("status", getStatus())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
