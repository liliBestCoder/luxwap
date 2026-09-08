package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.VpnLines;
import com.ruoyi.system.domain.VpnLinesExcel;

/**
 * VPN线路信息Service接口
 * 
 * @author ruoyi
 * @date 2025-08-09
 */
public interface IVpnLinesService 
{
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

    /**
     * 批量删除VPN线路信息
     * 
     * @param ids 需要删除的VPN线路信息主键集合
     * @return 结果
     */
    public int deleteVpnLinesByIds(String ids);

    /**
     * 删除VPN线路信息信息
     * 
     * @param id VPN线路信息主键
     * @return 结果
     */
    public int deleteVpnLinesById(String id);

    String batchImportVpnLineList(List<VpnLinesExcel> vpnLinesExcelList);

    public Map<String, Long> countLinesByStatus();

    public void pingStats();

    public List<String> generateVlessLinkList();

    public List<String> generateVlessLinkList(String userUuid);
}
