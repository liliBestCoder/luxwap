package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.XrayDistributorsConfig;

/**
 * 经销商配置Mapper接口
 *
 * @author ruoyi
 * @date 2025-09-04
 */
public interface XrayDistributorsConfigMapper
{
    /**
     * 查询经销商配置
     *
     * @param id 配置ID
     * @return 经销商配置
     */
    XrayDistributorsConfig selectXrayDistributorsConfigById(Long id);

    /**
     * 查询经销商配置列表
     *
     * @param config 经销商配置
     * @return 经销商配置集合
     */
    List<XrayDistributorsConfig> selectXrayDistributorsConfigList(XrayDistributorsConfig config);

    /**
     * 新增经销商配置
     *
     * @param config 经销商配置
     * @return 结果
     */
    int insertXrayDistributorsConfig(XrayDistributorsConfig config);

    /**
     * 修改经销商配置
     *
     * @param config 经销商配置
     * @return 结果
     */
    int updateXrayDistributorsConfig(XrayDistributorsConfig config);

    /**
     * 删除经销商配置
     *
     * @param id 配置ID
     * @return 结果
     */
    int deleteXrayDistributorsConfigById(Long id);

    /**
     * 批量删除经销商配置
     *
     * @param ids 配置ID数组
     * @return 结果
     */
    int deleteXrayDistributorsConfigByIds(Long[] ids);
}
