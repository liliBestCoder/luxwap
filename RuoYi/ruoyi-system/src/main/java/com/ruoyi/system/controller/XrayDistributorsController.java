package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.XrayDistributors;
import com.ruoyi.system.domain.XrayDistributorsConfig;
import com.ruoyi.system.service.IXrayDistributorsService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 经销商Controller
 * 
 * @author ruoyi
 * @date 2025-08-21
 */
@Controller
@RequestMapping("/system/distributors")
public class XrayDistributorsController extends BaseController
{

    /**
     * 带防重复机制的8位随机绑定码生成器
     */
    public static class BindingCodeGenerator {

        private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        private static final Random RANDOM = new Random();
        private static final Set<String> USED_CODES = ConcurrentHashMap.newKeySet();

        /**
         * 生成唯一的8位随机绑定码 (由A-Z, 0-9组成)
         * @return 8位唯一随机字符串
         */
        public static String generateUniqueBindingCode() {
            String code;
            do {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
                }
                code = sb.toString();
            } while (!USED_CODES.add(code)); // 如果code已存在，add会返回false，继续循环

            return code;
        }

        /**
         * 清除已生成的绑定码记录（可选）
         */
        public static void clearUsedCodes() {
            USED_CODES.clear();
        }
    }


    @Autowired
    private IXrayDistributorsService xrayDistributorsService;

    @RequiresPermissions("system:distributors:view")
    @GetMapping()
    public String distributors()
    {
        return "pages/distributors";
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XrayDistributors xrayDistributors)
    {
        startPage();
        List<XrayDistributors> list = xrayDistributorsService.selectXrayDistributorsList(xrayDistributors);
        return getDataTable(list);
    }

    @Log(title = "经销商", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(XrayDistributors xrayDistributors)
    {
        List<XrayDistributors> list = xrayDistributorsService.selectXrayDistributorsList(xrayDistributors);
        ExcelUtil<XrayDistributors> util = new ExcelUtil<XrayDistributors>(XrayDistributors.class);
        return util.exportExcel(list, "经销商数据");
    }

    @Log(title = "经销商", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    @ResponseBody
    public AjaxResult approve(@RequestParam Long id, @RequestParam String status)
    {
        if(!status.equals("approved") && !status.equals("rejected")) {
            return AjaxResult.error("状态非法");
        }

        XrayDistributors xrayDistributorsExist = xrayDistributorsService.selectXrayDistributorsById(id);
        if(xrayDistributorsExist == null && status.equals(xrayDistributorsExist.getStatus())){
            return AjaxResult.error("非法请求");
        }

        xrayDistributorsExist.setStatus(status);

        return toAjax(xrayDistributorsService.updateXrayDistributors(xrayDistributorsExist, getLoginName()));
    }

    @GetMapping("/stats")
    @ResponseBody
    public AjaxResult getStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("stats", xrayDistributorsService.getStats());
        data.put("levels", xrayDistributorsService.getLevelCounts());
        data.put("trend", xrayDistributorsService.getRegionTrends(12));
        return AjaxResult.success(data);
    }

    @GetMapping("/settings")
    @ResponseBody
    public AjaxResult detail()
    {
        List<XrayDistributorsConfig> list = xrayDistributorsService.selectXrayDistributorsConfigList(new XrayDistributorsConfig());
        return CollectionUtils.isEmpty(list) ? success() : success(list.get(0));
    }

    @PostMapping("/save-settings")
    @ResponseBody
    public AjaxResult saveSettings(@RequestParam Long id,
                                   @RequestParam Long commissionRate,
                                   @RequestParam Long firstChargeBonus)
    {
        xrayDistributorsService.saveSettings(id, commissionRate, firstChargeBonus);
        return AjaxResult.success();
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public AjaxResult editSave(@PathVariable Long id)
    {
        return AjaxResult.success(xrayDistributorsService.selectXrayDistributorsById(id));
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@RequestBody XrayDistributors xrayDistributors)
    {
        XrayDistributors exists = xrayDistributorsService.selectXrayDistributorsById(xrayDistributors.getId());
        if(exists == null){
            return AjaxResult.error("经销商不存在!");
        }
        exists.setCommissionRate(xrayDistributors.getCommissionRate());
        exists.setFirstChargeBonus(xrayDistributors.getFirstChargeBonus());

        return toAjax(xrayDistributorsService.updateXrayDistributors(exists, getLoginName()));
    }
}
