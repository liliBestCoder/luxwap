package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.XrayChainProxyNode;
import com.ruoyi.system.domain.vo.ChainProxyNodeVO;
import com.ruoyi.system.mapper.XrayChainProxyNodeMapper;
import com.ruoyi.system.service.IXrayChainProxyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 链式代理上游端点管理控制台Controller
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/chain-proxy")
public class XrayChainProxyNodeController extends BaseController {

    private String prefix = "pages/chainproxy";

    @Autowired
    private XrayChainProxyNodeMapper nodeMapper;

    @Autowired
    private IXrayChainProxyService chainProxyService;

    @RequiresPermissions("system:chainproxy:view")
    @GetMapping()
    public String chainProxy() {
        return prefix;
    }

    /**
     * 查询上游端点列表（后台分页）
     */
    @RequiresPermissions("system:chainproxy:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XrayChainProxyNode node) {
        startPage();
        List<XrayChainProxyNode> list = nodeMapper.selectXrayChainProxyNodeList(node);
        List<ChainProxyNodeVO> voList = list.stream().map(ChainProxyNodeVO::fromEntity).collect(Collectors.toList());
        return getDataTable(voList);
    }

    /**
     * 删除端点
     */
    @RequiresPermissions("system:chainproxy:remove")
    @Log(title = "链式代理端点", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        Long[] idArray = com.ruoyi.common.core.text.Convert.toLongArray(ids);
        return toAjax(nodeMapper.deleteXrayChainProxyNodeByIds(idArray, null));
    }
}
