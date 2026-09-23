package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Xray 经销商配置对象 xray_distributors_config
 *
 * @author ruoyi
 * @date 2025-09-04
 */
public class XrayDistributorsConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private static final long BYTES_PER_GB = 1073741824L;

    /** 配置ID */
    private Long id;

    /** 佣金比例（%） */
    @Excel(name = "佣金比例")
    private Long commissionRate;

    /** 首充返佣（%） */
    @Excel(name = "首充返佣")
    private Long firstChargeBonus;

    /** 首充赠送流量（字节） */
    @Excel(name = "首充赠送流量")
    private Long firstChargeBonusTraffic;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updatedAt;

    // ======== Getter & Setter ========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(Long commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Long getFirstChargeBonus() {
        return firstChargeBonus;
    }

    public void setFirstChargeBonus(Long firstChargeBonus) {
        this.firstChargeBonus = firstChargeBonus;
    }

    public Long getFirstChargeBonusTraffic() {
        return firstChargeBonusTraffic;
    }

    public void setFirstChargeBonusTraffic(Long firstChargeBonusTraffic) {
        this.firstChargeBonusTraffic = firstChargeBonusTraffic;
    }

    /** 首充赠送流量按 GB 对外展示与配置。 */
    public Long getBonusTrafficGb() {
        return firstChargeBonusTraffic == null ? null : firstChargeBonusTraffic / BYTES_PER_GB;
    }

    public void setBonusTrafficGb(Long bonusTrafficGb) {
        this.firstChargeBonusTraffic = bonusTrafficGb == null ? null : bonusTrafficGb * BYTES_PER_GB;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("commissionRate", getCommissionRate())
                .append("firstChargeBonus", getFirstChargeBonus())
                .append("createdAt", getCreatedAt())
                .append("updatedAt", getUpdatedAt())
                .toString();
    }
}
