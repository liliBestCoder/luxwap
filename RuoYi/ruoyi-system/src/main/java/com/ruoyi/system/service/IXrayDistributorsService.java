package com.ruoyi.system.service;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.system.domain.XrayDistributors;
import com.ruoyi.system.domain.XrayDistributorsConfig;

/**
 * 经销商Service接口
 * 
 * @author ruoyi
 * @date 2025-08-21
 */
public interface IXrayDistributorsService 
{
    /**
     * 查询经销商
     * 
     * @param id 经销商主键
     * @return 经销商
     */
    public XrayDistributors selectXrayDistributorsById(Long id);

    /**
     * 查询经销商列表
     * 
     * @param xrayDistributors 经销商
     * @return 经销商集合
     */
    public List<XrayDistributors> selectXrayDistributorsList(XrayDistributors xrayDistributors);

    /**
     * 新增经销商
     * 
     * @param xrayDistributors 经销商
     * @return 结果
     */
    public int insertXrayDistributors(XrayDistributors xrayDistributors);

    /**
     * 修改经销商
     * 
     * @param xrayDistributors 经销商
     * @return 结果
     */
    public int updateXrayDistributors(XrayDistributors xrayDistributors, String currentUser);

    /**
     * 批量删除经销商
     * 
     * @param ids 需要删除的经销商主键集合
     * @return 结果
     */
    public int deleteXrayDistributorsByIds(String ids);

    /**
     * 删除经销商信息
     * 
     * @param id 经销商主键
     * @return 结果
     */
    public int deleteXrayDistributorsById(String id);

    public XrayDistributors.DistributorStatsVO getStats();

    public List<XrayDistributors.LevelCountVO> getLevelCounts();

    public List<XrayDistributors.RegionTrendVO> getRegionTrends(int months);

    public List<XrayDistributorsConfig> selectXrayDistributorsConfigList(XrayDistributorsConfig xrayDistributorsConfig);

    public void saveSettings(Long id, Long commissionRate, Long firstChargeBonus);
}
