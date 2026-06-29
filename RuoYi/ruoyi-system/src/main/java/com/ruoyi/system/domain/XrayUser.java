package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户管理对象 xray_user
 * 
 * @author ruoyi
 * @date 2025-08-11
 */
public class XrayUser extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 用户唯一标识符，UUID */
    @Excel(name = "用户唯一标识符，UUID")
    private String uuid;

    /** 用户名，唯一 */
    @Excel(name = "用户名，唯一")
    private String username;

    @Excel(name = "昵称")
    private String nick;

    @Excel(name = "国家")
    private String country;

    @Excel(name = "设备")
    private String deviceId;

    /** 加密后的密码 */
    @Excel(name = "加密后的密码")
    private String password;

    /** 真实邮箱，可选 */
    @Excel(name = "真实邮箱，可选")
    private String email;

    /** 内部使用的唯一邮箱，不对外暴露 */
    @Excel(name = "内部使用的唯一邮箱，不对外暴露")
    private String uniqueEmail;

    /** 第三方平台名称或本地登录 */
    @Excel(name = "第三方平台名称或本地登录")
    private String provider;

    /** 第三方平台用户唯一标识符 */
    @Excel(name = "第三方平台用户唯一标识符")
    private String providerUserId;

    /** 第三方平台上的用户名（昵称） */
    @Excel(name = "第三方平台上的用户名", readConverterExp = "昵=称")
    private String providerUsername;

    /** 第三方平台用户头像 URL */
    @Excel(name = "第三方平台用户头像 URL")
    private String providerAvatarUrl;

    /** 账户状态：1 启用，0 禁用 */
    @Excel(name = "账户状态：1 启用，0 禁用")
    private Integer status;

    /** 总流量，单位为字节 */
    @Excel(name = "当月使用流量，单位为字节")
    private Long usedTraffic;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expiration;

    @Excel(name = "累冲月数")
    private Integer cumulativeMonths ;

    @Excel(name = "用户类型")
    private String type;

    @Excel(name = "邀请码")
    private String inviteCode;

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

    public void setUuid(String uuid) 
    {
        this.uuid = uuid;
    }

    public String getUuid() 
    {
        return uuid;
    }

    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }

    public void setEmail(String email) 
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }

    public void setUniqueEmail(String uniqueEmail)
    {
        this.uniqueEmail = uniqueEmail;
    }

    public String getUniqueEmail() 
    {
        return uniqueEmail;
    }

    public void setProvider(String provider) 
    {
        this.provider = provider;
    }

    public String getProvider() 
    {
        return provider;
    }

    public void setProviderUserId(String providerUserId) 
    {
        this.providerUserId = providerUserId;
    }

    public String getProviderUserId() 
    {
        return providerUserId;
    }

    public void setProviderUsername(String providerUsername) 
    {
        this.providerUsername = providerUsername;
    }

    public String getProviderUsername() 
    {
        return providerUsername;
    }

    public void setProviderAvatarUrl(String providerAvatarUrl) 
    {
        this.providerAvatarUrl = providerAvatarUrl;
    }

    public String getProviderAvatarUrl() 
    {
        return providerAvatarUrl;
    }

    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    public Long getUsedTraffic() {
        return usedTraffic;
    }

    public void setUsedTraffic(Long usedTraffic) {
        this.usedTraffic = usedTraffic;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public Integer getCumulativeMonths() {
        return cumulativeMonths;
    }

    public void setCumulativeMonths(Integer cumulativeMonths) {
        this.cumulativeMonths = cumulativeMonths;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
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
            .append("uuid", getUuid())
            .append("username", getUsername())
            .append("password", getPassword())
            .append("email", getEmail())
            .append("uniqueEmail", getUniqueEmail())
            .append("provider", getProvider())
            .append("providerUserId", getProviderUserId())
            .append("providerUsername", getProviderUsername())
            .append("providerAvatarUrl", getProviderAvatarUrl())
            .append("status", getStatus())
            .append("usedTraffic", getUsedTraffic())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
