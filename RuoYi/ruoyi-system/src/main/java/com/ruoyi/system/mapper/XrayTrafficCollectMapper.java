package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.XrayTrafficCollect;
import org.apache.ibatis.annotations.Select;

/**
 * 流量数据上报Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
public interface XrayTrafficCollectMapper 
{

    @Select("SELECT client_ip, SUM(val) AS used_traffic FROM xray_traffic_collect where level = 'inbound' GROUP BY client_ip")
    List<Map<String, Object>> statTrafficByClientIp();

    @Select("SELECT email, SUM(val) AS used_traffic FROM xray_traffic_collect where level = 'user' GROUP BY email")
    List<Map<String, Object>> statTrafficByEmail();

    public void saveXrayTrafficCollectList(List<XrayTrafficCollect> trafficCollectList);
    /**
     * 查询流量数据上报
     * 
     * @param id 流量数据上报主键
     * @return 流量数据上报
     */
    public XrayTrafficCollect selectXrayTrafficCollectById(Long id);

    /**
     * 查询流量数据上报列表
     * 
     * @param xrayTrafficCollect 流量数据上报
     * @return 流量数据上报集合
     */
    public List<XrayTrafficCollect> selectXrayTrafficCollectList(XrayTrafficCollect xrayTrafficCollect);

    /**
     * 新增流量数据上报
     * 
     * @param xrayTrafficCollect 流量数据上报
     * @return 结果
     */
    public int insertXrayTrafficCollect(XrayTrafficCollect xrayTrafficCollect);

    /**
     * 修改流量数据上报
     * 
     * @param xrayTrafficCollect 流量数据上报
     * @return 结果
     */
    public int updateXrayTrafficCollect(XrayTrafficCollect xrayTrafficCollect);

    /**
     * 删除流量数据上报
     * 
     * @param id 流量数据上报主键
     * @return 结果
     */
    public int deleteXrayTrafficCollectById(Long id);

    /**
     * 批量删除流量数据上报
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteXrayTrafficCollectByIds(String[] ids);
}
