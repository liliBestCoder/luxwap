package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.VpnLines;
import com.ruoyi.system.domain.VpnLinesExcel;
import com.ruoyi.system.service.IVpnLinesService;
import com.ruoyi.system.util.CsvUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * VPN线路信息Controller
 * 
 * @author ruoyi
 * @date 2025-08-09
 */
@Controller
@RequestMapping("/system/lines")
public class VpnLinesController extends BaseController
{
    private String prefix = "system/lines";

    @Autowired
    private IVpnLinesService vpnLinesService;

    @RequiresPermissions("system:lines:view")
    @GetMapping()
    public String lines()
    {
        return "pages/lines";
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(VpnLines vpnLines)
    {
        startPage();
        List<VpnLines> list = vpnLinesService.selectVpnLinesList(vpnLines);
        return getDataTable(list);
    }

    @Log(title = "VPN线路信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(VpnLines vpnLines)
    {
        List<VpnLines> list = vpnLinesService.selectVpnLinesList(vpnLines);
        ExcelUtil<VpnLines> util = new ExcelUtil<VpnLines>(VpnLines.class);
        return util.exportExcel(list, "VPN线路信息数据");
    }

    @Log(title = "VPN线路信息", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    @ResponseBody
    public AjaxResult importVpnLines(@RequestParam("file") MultipartFile file) throws Exception
    {
        List<VpnLinesExcel> vpnLinesExcelList = CsvUtils.readCsvToVpnLine(file.getInputStream());
        return AjaxResult.success(vpnLinesService.batchImportVpnLineList(vpnLinesExcelList));
    }


    @GetMapping("/stats")
    @Log(title = "VPN线路信息", businessType = BusinessType.STATS)
    @ResponseBody
    public AjaxResult countLinesByStatus() {
        Map<String, Long> result = vpnLinesService.countLinesByStatus();
        return AjaxResult.success(result);
    }

    @Log(title = "VPN线路信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(VpnLines vpnLines)
    {
        return toAjax(vpnLinesService.updateVpnLines(vpnLines));
    }

    @RequiresPermissions("system:lines:remove")
    @Log(title = "VPN线路信息", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(vpnLinesService.deleteVpnLinesByIds(ids));
    }
}
