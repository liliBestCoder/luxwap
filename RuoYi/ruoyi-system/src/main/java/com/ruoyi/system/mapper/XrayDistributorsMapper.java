package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.XrayDistributors;
import org.apache.ibatis.annotations.Param;

/**
 * 经销商Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-21
 */
public interface XrayDistributorsMapper 
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
    public int updateXrayDistributors(XrayDistributors xrayDistributors);

    /**
     * 删除经销商
     * 
     * @param id 经销商主键
     * @return 结果
     */
    public int deleteXrayDistributorsById(String id);

    /**
     * 批量删除经销商
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteXrayDistributorsByIds(String[] ids);

    XrayDistributors.DistributorStatsVO getDistributorStats();

    List<XrayDistributors.LevelCountVO> getLevelCounts();

    List<XrayDistributors.RegionTrendVO> getRegionTrends(@Param("months") int months);
}
