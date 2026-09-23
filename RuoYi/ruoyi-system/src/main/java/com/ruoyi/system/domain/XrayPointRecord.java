package com.ruoyi.system.domain;

import java.util.Date;

/**
 * 积分流水。
 */
public class XrayPointRecord {
    /** 按流量用量发放 */
    public static final String TYPE_EARN_TRAFFIC = "EARN_TRAFFIC";
    /** 消耗积分兑换流量 */
    public static final String TYPE_EXCHANGE_TRAFFIC = "EXCHANGE_TRAFFIC";

    private Long id;
    private Long userId;
    private Long changeAmount;
    private Long balanceAfter;
    private String type;
    private String remark;
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Long changeAmount) {
        this.changeAmount = changeAmount;
    }

    public Long getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(Long balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
