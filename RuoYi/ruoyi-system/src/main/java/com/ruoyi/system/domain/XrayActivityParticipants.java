package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 activity_participants
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
public class XrayActivityParticipants extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 自增ID */
    private Long id;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 注册时间，默认当前时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "注册时间，默认当前时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date registrationTime;

    /** 用户Email */
    @Excel(name = "用户Email")
    private String userEmail;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 会员：0-非会员，1-会员 */
    @Excel(name = "会员：0-非会员，1-会员")
    private Long member;

    /** 有效期，会员有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "有效期，会员有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expiration;

    /** 名次，活动中的名次 */
    @Excel(name = "名次，活动中的名次")
    private Long rank;

    /** 用户类型，如团队、部门等 */
    @Excel(name = "用户类型，如团队、部门等")
    private String type;

    /** 关联活动ID */
    @Excel(name = "关联活动ID")
    private Long activityId;

    /** 审核链接 */
    @Excel(name = "审核链接")
    private String auditLink;

    /** 记录创建时间，默认当前时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "记录创建时间，默认当前时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 记录更新时间，自动更新 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "记录更新时间，自动更新", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setRegistrationTime(Date registrationTime) 
    {
        this.registrationTime = registrationTime;
    }

    public Date getRegistrationTime() 
    {
        return registrationTime;
    }

    public void setUserEmail(String userEmail) 
    {
        this.userEmail = userEmail;
    }

    public String getUserEmail() 
    {
        return userEmail;
    }

    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public void setMember(Long member) 
    {
        this.member = member;
    }

    public Long getMember() 
    {
        return member;
    }

    public void setExpiration(Date expiration) 
    {
        this.expiration = expiration;
    }

    public Date getExpiration() 
    {
        return expiration;
    }

    public void setRank(Long rank) 
    {
        this.rank = rank;
    }

    public Long getRank() 
    {
        return rank;
    }

    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }

    public void setActivityId(Long activityId) 
    {
        this.activityId = activityId;
    }

    public Long getActivityId() 
    {
        return activityId;
    }

    public void setAuditLink(String auditLink) 
    {
        this.auditLink = auditLink;
    }

    public String getAuditLink() 
    {
        return auditLink;
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
            .append("userId", getUserId())
            .append("registrationTime", getRegistrationTime())
            .append("userEmail", getUserEmail())
            .append("userName", getUserName())
            .append("member", getMember())
            .append("expiration", getExpiration())
            .append("rank", getRank())
            .append("type", getType())
            .append("activityId", getActivityId())
            .append("auditLink", getAuditLink())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
