package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.XrayChainProxyConfig;
import org.apache.ibatis.annotations.Param;

/**
 * 用户链式代理激活配置Mapper接口
 *
 * @author ruoyi
 */
public interface XrayChainProxyConfigMapper {

    /**
     * 查询用户链式代理配置
     */
    XrayChainProxyConfig selectConfigByUserId(@Param("userId") Long userId);

    /**
     * 新增用户链式代理配置
     */
    int insertConfig(XrayChainProxyConfig config);

    /**
     * 更新用户链式代理配置
     */
    int updateConfig(XrayChainProxyConfig config);
}
