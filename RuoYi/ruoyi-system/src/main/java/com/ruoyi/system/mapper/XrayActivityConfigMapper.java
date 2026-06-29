package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.XrayActivityConfig;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
public interface XrayActivityConfigMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public XrayActivityConfig selectXrayActivityConfigById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param xrayActivityConfig 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<XrayActivityConfig> selectXrayActivityConfigList(XrayActivityConfig xrayActivityConfig);

    /**
     * 新增【请填写功能名称】
     * 
     * @param xrayActivityConfig 【请填写功能名称】
     * @return 结果
     */
    public int insertXrayActivityConfig(XrayActivityConfig xrayActivityConfig);

    /**
     * 修改【请填写功能名称】
     * 
     * @param xrayActivityConfig 【请填写功能名称】
     * @return 结果
     */
    public int updateXrayActivityConfig(XrayActivityConfig xrayActivityConfig);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteXrayActivityConfigById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteXrayActivityConfigByIds(String[] ids);
}
