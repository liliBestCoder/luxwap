package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.threadlocal.XrayThreadLocal;
import com.ruoyi.system.domain.XrayChainProxyConfig;
import com.ruoyi.system.domain.dto.ChainProxyActiveConfigDTO;
import com.ruoyi.system.domain.dto.ChainProxyImportDTO;
import com.ruoyi.system.domain.dto.ChainProxyStatusReportDTO;
import com.ruoyi.system.domain.vo.ChainProxyNodeVO;
import com.ruoyi.common.utils.IpUtils;

import javax.servlet.http.HttpServletRequest;
import com.ruoyi.system.service.IXrayChainProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 客户端链式代理上游端点与激活配置 API 控制器
 * 接口路径统一在 /api/client/**，由 ClientJwtFilter 校验 Token 并注入 XrayThreadLocal
 * 
 * 架构特性：
 * 1. 采用非自增安全业务唯一 Key (nodeKey) 防止 ID 遍历与爬虫攻击
 * 2. 测活与延迟由客户端本地网络环境真实发起，服务端仅做结果同步存储，不进行低效不准确的服务端探测
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/client/chain-proxy")
public class ClientChainProxyController extends BaseController {

    @Autowired
    private IXrayChainProxyService chainProxyService;

    /**
     * 回显调用方的出口 IP。
     *
     * <p>客户端经待检测的代理请求本接口，服务端看到的源 IP 即该代理的出口 IP。
     * 由本服务自己充当回显方，可免去对第三方回显服务的依赖、配额与使用条款约束。
     */
    @GetMapping("/echo-ip")
    public AjaxResult echoIp(HttpServletRequest request) {
        AjaxResult result = AjaxResult.success();
        result.put("ip", IpUtils.getIpAddr(request));
        return result;
    }

    /**
     * 查询当前用户可用的上游端点列表
     */
    @GetMapping("/list")
    public AjaxResult list(@RequestParam(value = "protocol", required = false) String protocol,
                           @RequestParam(value = "aliveStatus", required = false) Integer aliveStatus,
                           @RequestParam(value = "countryCode", required = false) String countryCode,
                           @RequestParam(value = "remark", required = false) String remark) {
        Long userId = XrayThreadLocal.getUid();
        if (userId == null) {
            return AjaxResult.error("未认证的用户会话");
        }
        List<ChainProxyNodeVO> list = chainProxyService.queryUserProxyList(userId, protocol, aliveStatus, countryCode, remark);
        return AjaxResult.success(list);
    }

    /**
     * 批量导入上游端点（单次最多 30 条）
     */
    @PostMapping("/batch-import")
    public AjaxResult batchImport(@RequestBody ChainProxyImportDTO dto) {
        Long userId = XrayThreadLocal.getUid();
        if (userId == null) {
            return AjaxResult.error("未认证的用户会话");
        }
        Map<String, Object> result = chainProxyService.batchImportProxies(userId, dto);
        return AjaxResult.success(result);
    }

    /**
     * 同步客户端本地真实测活与延迟结果
     * 由客户端在本地发起握手与测延迟后将结果上报服务端保存
     */
    @PostMapping("/report-status")
    public AjaxResult reportStatus(@RequestBody List<ChainProxyStatusReportDTO> reports) {
        Long userId = XrayThreadLocal.getUid();
        if (userId == null) {
            return AjaxResult.error("未认证的用户会话");
        }
        chainProxyService.updateNodeStatusFromClient(userId, reports);
        return AjaxResult.success("状态已同步");
    }

    /**
     * 获取当前用户的链式代理激活配置
     */
    @GetMapping("/config")
    public AjaxResult getConfig() {
        Long userId = XrayThreadLocal.getUid();
        if (userId == null) {
            return AjaxResult.error("未认证的用户会话");
        }
        XrayChainProxyConfig config = chainProxyService.getUserChainConfig(userId);
        return AjaxResult.success(config);
    }

    /**
     * 更新当前用户的链式代理激活配置（支持绑定 activeNodeKey）
     */
    @PostMapping("/config")
    public AjaxResult updateConfig(@RequestBody ChainProxyActiveConfigDTO dto) {
        Long userId = XrayThreadLocal.getUid();
        if (userId == null) {
            return AjaxResult.error("未认证的用户会话");
        }
        chainProxyService.saveUserChainConfig(userId, dto);
        return AjaxResult.success("链式代理配置已更新");
    }

    /**
     * 删除单个上游端点（基于对外业务安全唯一 Key，彻底杜绝自增 ID 遍历）
     */
    @DeleteMapping("/{nodeKey}")
    public AjaxResult remove(@PathVariable("nodeKey") String nodeKey) {
        Long userId = XrayThreadLocal.getUid();
        if (userId == null) {
            return AjaxResult.error("未认证的用户会话");
        }
        boolean success = chainProxyService.deleteProxyByKey(userId, nodeKey);
        return toAjax(success);
    }

    /**
     * 批量删除上游端点（基于对外业务安全唯一 Keys）
     */
    @PostMapping("/batch-delete")
    public AjaxResult batchDelete(@RequestBody String[] nodeKeys) {
        Long userId = XrayThreadLocal.getUid();
        if (userId == null) {
            return AjaxResult.error("未认证的用户会话");
        }
        boolean success = chainProxyService.batchDeleteProxiesByKeys(userId, nodeKeys);
        return toAjax(success);
    }
}
