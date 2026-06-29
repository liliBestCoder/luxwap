package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 经销商对象 xray_distributors
 * 
 * @author ruoyi
 * @date 2025-08-21
 */
public class XrayDistributors extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分销商ID */
    private Long id;

    private Long userId;


    /** 分销商Email */
    @Excel(name = "分销商Email")
    private String email;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactPerson;

    /** 等级（钻石/黄金/白银/普通） */
    @Excel(name = "等级")
    private String level;

    /** 月销售额 */
    @Excel(name = "月销售额")
    private BigDecimal monthlySales;

    /** 佣金比例（%），整数存储，如 10 表示10% */
    @Excel(name = "佣金比例")
    private Long commissionRate;

    /** 首充返佣（%），整数存储，如 30 表示30% */
    @Excel(name = "首充返佣")
    private Long firstChargeBonus;

    /** 收款Paypal */
    @Excel(name = "收款Paypal")
    private String paypalAccount;

    /** 绑定码 */
    @Excel(name = "绑定码")
    private String bindingCode;

    /** 专属后缀 */
    @Excel(name = "专属后缀")
    private String exclusiveSuffix;

    /** 地区（中国/台湾/越南/俄罗斯/韩国/美国/日本） */
    @Excel(name = "地区")
    private String region;

    /** 状态（pending:初始化，approved:审批通过，rejected:审批不通过） */
    @Excel(name = "状态")
    private String status;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getEmail() 
    {
        return email;
    }

    public void setContactPerson(String contactPerson) 
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson() 
    {
        return contactPerson;
    }

    public void setLevel(String level) 
    {
        this.level = level;
    }

    public String getLevel() 
    {
        return level;
    }

    public void setMonthlySales(BigDecimal monthlySales) 
    {
        this.monthlySales = monthlySales;
    }

    public BigDecimal getMonthlySales() 
    {
        return monthlySales;
    }

    public void setCommissionRate(Long commissionRate) 
    {
        this.commissionRate = commissionRate;
    }

    public Long getCommissionRate() 
    {
        return commissionRate;
    }

    public void setFirstChargeBonus(Long firstChargeBonus) 
    {
        this.firstChargeBonus = firstChargeBonus;
    }

    public Long getFirstChargeBonus() 
    {
        return firstChargeBonus;
    }

    public void setPaypalAccount(String paypalAccount) 
    {
        this.paypalAccount = paypalAccount;
    }

    public String getPaypalAccount() 
    {
        return paypalAccount;
    }

    public void setBindingCode(String bindingCode) 
    {
        this.bindingCode = bindingCode;
    }

    public String getBindingCode() 
    {
        return bindingCode;
    }

    public void setExclusiveSuffix(String exclusiveSuffix) 
    {
        this.exclusiveSuffix = exclusiveSuffix;
    }

    public String getExclusiveSuffix() 
    {
        return exclusiveSuffix;
    }

    public void setRegion(String region) 
    {
        this.region = region;
    }

    public String getRegion() 
    {
        return region;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
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
            .append("email", getEmail())
            .append("contactPerson", getContactPerson())
            .append("level", getLevel())
            .append("monthlySales", getMonthlySales())
            .append("commissionRate", getCommissionRate())
            .append("firstChargeBonus", getFirstChargeBonus())
            .append("paypalAccount", getPaypalAccount())
            .append("bindingCode", getBindingCode())
            .append("exclusiveSuffix", getExclusiveSuffix())
            .append("region", getRegion())
            .append("status", getStatus())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }

    public static class DistributorStatsVO {
        private Long total;          // 总数
        private Long active;         // 活跃数
        private Long newThisMonth;

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Long getActive() {
            return active;
        }

        public void setActive(Long active) {
            this.active = active;
        }

        public Long getNewThisMonth() {
            return newThisMonth;
        }

        public void setNewThisMonth(Long newThisMonth) {
            this.newThisMonth = newThisMonth;
        }

        // 本月新增
    }

    public static class LevelCountVO {
        private String level;
        private Long count;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }

    public static class RegionTrendVO {
        private String region;
        private String month;
        private BigDecimal totalSales;

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public BigDecimal getTotalSales() {
            return totalSales;
        }

        public void setTotalSales(BigDecimal totalSales) {
            this.totalSales = totalSales;
        }
    }
}
