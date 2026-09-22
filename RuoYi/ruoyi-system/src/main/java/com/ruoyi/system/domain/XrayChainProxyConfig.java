package com.ruoyi.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户链式代理激活配置对象 xray_chain_proxy_config
 *
 * @author ruoyi
 */
public class XrayChainProxyConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 是否启用链式代理：0-关闭，1-启用 */
    private Integer isEnabled;

    /** 当前激活的上游跳板端点ID（关联 xray_chain_proxy_node.id） */
    private Long activeNodeId;

    /** 当前激活的上游跳板端点安全Key（关联 xray_chain_proxy_node.node_key） */
    private String activeNodeKey;

    /** 绑定的落地出口线路ID（关联 vpn_lines.id） */
    private Long exitLineId;

    /** 激活模式：fixed_exit(固定出口), auto_best(智能优选) */
    private String mode;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

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

    public Integer getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Long getActiveNodeId() {
        return activeNodeId;
    }

    public void setActiveNodeId(Long activeNodeId) {
        this.activeNodeId = activeNodeId;
    }

    public String getActiveNodeKey() {
        return activeNodeKey;
    }

    public void setActiveNodeKey(String activeNodeKey) {
        this.activeNodeKey = activeNodeKey;
    }

    public Long getExitLineId() {
        return exitLineId;
    }

    public void setExitLineId(Long exitLineId) {
        this.exitLineId = exitLineId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
