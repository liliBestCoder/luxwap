package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.VpnLines;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * VPN线路信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-09
 */
public interface VpnLinesMapper 
{

    int updateTrafficByClientIp(@Param("clientIp") String clientIp, @Param("usedTraffic") Integer usedTraffic);
    /**
     * 查询VPN线路信息
     * 
     * @param id VPN线路信息主键
     * @return VPN线路信息
     */
    public VpnLines selectVpnLinesById(Long id);

    /**
     * 查询VPN线路信息列表
     * 
     * @param vpnLines VPN线路信息
     * @return VPN线路信息集合
     */
    public List<VpnLines> selectVpnLinesList(VpnLines vpnLines);

    /**
     * 新增VPN线路信息
     * 
     * @param vpnLines VPN线路信息
     * @return 结果
     */
    public int insertVpnLines(VpnLines vpnLines);

    /**
     * 修改VPN线路信息
     * 
     * @param vpnLines VPN线路信息
     * @return 结果
     */
    public int updateVpnLines(VpnLines vpnLines);


    public int updateStatusByClientIp(@Param("clientIp") String clientIp, @Param("status") String status);

    /**
     * 删除VPN线路信息
     * 
     * @param id VPN线路信息主键
     * @return 结果
     */
    public int deleteVpnLinesById(String id);

    /**
     * 批量删除VPN线路信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVpnLinesByIds(String[] ids);

    /**
     * 统计每个状态下的线路数量
     *
     * @return Map<String, Integer>  返回每个状态的线路数量
     */
    @Select("SELECT status, COUNT(*) AS count FROM vpn_lines GROUP BY status")
    public List<Map<String, Object>> countLinesByStatus();
}
