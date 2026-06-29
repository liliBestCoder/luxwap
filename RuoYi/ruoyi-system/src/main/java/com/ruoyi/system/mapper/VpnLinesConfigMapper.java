package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.VpnLinesConfig;

/**
 * VPN线路配置（存储xray的config.json）Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-13
 */
public interface VpnLinesConfigMapper 
{
    /**
     * 查询VPN线路配置（存储xray的config.json）
     * 
     * @param id VPN线路配置（存储xray的config.json）主键
     * @return VPN线路配置（存储xray的config.json）
     */
    public VpnLinesConfig selectVpnLinesConfigById(String id);

    /**
     * 查询VPN线路配置（存储xray的config.json）列表
     * 
     * @param vpnLinesConfig VPN线路配置（存储xray的config.json）
     * @return VPN线路配置（存储xray的config.json）集合
     */
    public List<VpnLinesConfig> selectVpnLinesConfigList(VpnLinesConfig vpnLinesConfig);

    /**
     * 新增VPN线路配置（存储xray的config.json）
     * 
     * @param vpnLinesConfig VPN线路配置（存储xray的config.json）
     * @return 结果
     */
    public int insertVpnLinesConfig(VpnLinesConfig vpnLinesConfig);

    /**
     * 修改VPN线路配置（存储xray的config.json）
     * 
     * @param vpnLinesConfig VPN线路配置（存储xray的config.json）
     * @return 结果
     */
    public int updateVpnLinesConfig(VpnLinesConfig vpnLinesConfig);

    public int updateVpnLinesConfigByVpnLineId(VpnLinesConfig vpnLinesConfig);

    /**
     * 删除VPN线路配置（存储xray的config.json）
     * 
     * @param id VPN线路配置（存储xray的config.json）主键
     * @return 结果
     */
    public int deleteVpnLinesConfigById(String id);

    /**
     * 批量删除VPN线路配置（存储xray的config.json）
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVpnLinesConfigByIds(String[] ids);
}
