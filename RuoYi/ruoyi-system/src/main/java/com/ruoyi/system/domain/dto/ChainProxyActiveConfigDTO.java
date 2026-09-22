package com.ruoyi.system.domain.dto;

import java.io.Serializable;

/**
 * 用户链式代理激活配置修改 DTO
 *
 * @author ruoyi
 */
public class ChainProxyActiveConfigDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否启用链式多跳：0-关闭，1-启用 */
    private Integer isEnabled;

    /** 同一开关的布尔写法，客户端以此字段发送。取值经 {@link #resolveEnabled()} 归一。 */
    private Boolean chainEnabled;

    /** 当前激活的上游跳板端点ID */
    private Long activeNodeId;

    /** 当前激活的上游跳板端点安全Key */
    private String activeNodeKey;

    /** 绑定的落地出口线路ID */
    private Long exitLineId;

    /** 模式：fixed_exit(固定出口), auto_best(智能优选) */
    private String mode;

    public Integer getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Integer isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Boolean getChainEnabled() {
        return chainEnabled;
    }

    public void setChainEnabled(Boolean chainEnabled) {
        this.chainEnabled = chainEnabled;
    }

    /**
     * 归一化启用状态。整数与布尔两种写法都可能出现，也可能都未提供。
     *
     * @return 1 启用、0 关闭；两者皆缺省时返回 null，表示本次不修改该字段
     */
    /**
     * 归一化激活模式。库中以小写形态存储（fixed_exit / auto_best），
     * 客户端历史上发送大写，这里统一折叠，避免同一语义存成两种值。
     *
     * @return 小写模式名；未提供时返回 null，表示本次不修改该字段
     */
    public String resolveMode() {
        if (mode == null) {
            return null;
        }
        String trimmed = mode.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase();
    }

    public Integer resolveEnabled() {
        if (isEnabled != null) {
            return isEnabled;
        }
        if (chainEnabled != null) {
            return chainEnabled ? 1 : 0;
        }
        return null;
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
}
