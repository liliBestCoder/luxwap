package com.ruoyi.system.service;

import com.ruoyi.system.domain.XrayChainProxyConfig;
import com.ruoyi.system.domain.dto.ChainProxyActiveConfigDTO;
import com.ruoyi.system.domain.dto.ChainProxyImportDTO;
import com.ruoyi.system.domain.dto.ChainProxyStatusReportDTO;
import com.ruoyi.system.domain.vo.ChainProxyNodeVO;

import java.util.List;
import java.util.Map;

/**
 * 链式代理业务层接口
 *
 * @author ruoyi
 */
public interface IXrayChainProxyService {

    /**
     * 查询用户可用的链式代理上游端点列表（含平台公共端点）
     */
    List<ChainProxyNodeVO> queryUserProxyList(Long userId, String protocol, Integer aliveStatus, String countryCode, String remark);

    /**
     * 批量解析与导入上游端点
     */
    Map<String, Object> batchImportProxies(Long userId, ChainProxyImportDTO dto);

    /**
     * 接收客户端本地真实测活与延迟探测结果并同步存储
     */
    void updateNodeStatusFromClient(Long userId, List<ChainProxyStatusReportDTO> reports);

    /**
     * 获取当前用户的链式代理激活配置
     */
    XrayChainProxyConfig getUserChainConfig(Long userId);

    /**
     * 保存/更新当前用户的链式代理激活配置
     */
    void saveUserChainConfig(Long userId, ChainProxyActiveConfigDTO dto);

    /**
     * 根据对外安全 Key 删除单个上游端点
     */
    boolean deleteProxyByKey(Long userId, String nodeKey);

    /**
     * 根据对外安全 Key 批量删除上游端点
     */
    boolean batchDeleteProxiesByKeys(Long userId, String[] nodeKeys);

    /**
     * 根据主键ID删除单个上游端点（管理端）
     */
    boolean deleteProxy(Long userId, Long nodeId);

    /**
     * 批量删除上游端点（管理端）
     */
    boolean batchDeleteProxies(Long userId, Long[] nodeIds);
}
