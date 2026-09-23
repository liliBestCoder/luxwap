package com.ruoyi.system.domain;

import java.util.Date;

/**
 * 用户积分账户。积分随已用流量增长发放，settledTraffic 是已经换算过积分的流量水位。
 */
public class XrayUserPoint {
    private Long id;
    private Long userId;
    private Long balance;
    private Long totalEarned;
    private Long settledTraffic;
    private Date createdAt;
    private Date updatedAt;

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

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(Long totalEarned) {
        this.totalEarned = totalEarned;
    }

    public Long getSettledTraffic() {
        return settledTraffic;
    }

    public void setSettledTraffic(Long settledTraffic) {
        this.settledTraffic = settledTraffic;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
