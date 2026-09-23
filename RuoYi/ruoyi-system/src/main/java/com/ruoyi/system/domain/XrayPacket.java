package com.ruoyi.system.domain;
import com.ruoyi.common.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

public class XrayPacket extends BaseEntity {
    private Long id;
    private String name;
    private Integer durationMonths;
    private Integer bonusMonths;
    /** 套餐流量(字节)，计费主口径 */
    private Long trafficBytes;
    /** 赠送流量(字节) */
    private Long bonusTrafficBytes;
    private BigDecimal price;
    private BigDecimal pricePerMonth;
    private BigDecimal pricePerGb;
    private String description;
    private Integer status;
    private Integer sortOrder;
    private Date createdAt;
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTrafficBytes() {
        return trafficBytes;
    }

    public void setTrafficBytes(Long trafficBytes) {
        this.trafficBytes = trafficBytes;
    }

    public Long getBonusTrafficBytes() {
        return bonusTrafficBytes;
    }

    public void setBonusTrafficBytes(Long bonusTrafficBytes) {
        this.bonusTrafficBytes = bonusTrafficBytes;
    }

    public BigDecimal getPricePerGb() {
        return pricePerGb;
    }

    public void setPricePerGb(BigDecimal pricePerGb) {
        this.pricePerGb = pricePerGb;
    }

    /** 该套餐一次性发放的总流量(含赠送)。 */
    public long totalTrafficBytes() {
        long base = trafficBytes == null ? 0L : trafficBytes;
        long bonus = bonusTrafficBytes == null ? 0L : bonusTrafficBytes;
        return base + bonus;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public Integer getBonusMonths() {
        return bonusMonths;
    }

    public void setBonusMonths(Integer bonusMonths) {
        this.bonusMonths = bonusMonths;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(BigDecimal pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
