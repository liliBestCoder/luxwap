package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.service.IXrayUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 用户管理Controller
 * 
 * @author ruoyi
 * @date 2025-08-11
 */
@Controller
@RequestMapping("/system/xray-user")
public class XrayUserController extends BaseController
{
    private String prefix = "system/xray-user";

    @Autowired
    private IXrayUserService xrayUserService;

    @RequiresPermissions("system:xray-user:view")
    @GetMapping()
    public String xrayUser()
    {
        return "pages/users";
    }

    public static class XrayUserListReq{
        private String search;
        private String belong;
        private String registerTime;

        public String getSearch() {
            return search;
        }

        public void setSearch(String search) {
            this.search = search;
        }

        public String getBelong() {
            return belong;
        }

        public void setBelong(String belong) {
            this.belong = belong;
        }

        public String getRegisterTime() {
            return registerTime;
        }

        public void setRegisterTime(String registerTime) {
            this.registerTime = registerTime;
        }
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(XrayUserListReq req)
    {
        startPage();
        String search = req.getSearch();
        String belong = req.getBelong();
        String registerTime = req.getRegisterTime();
        Date startTime = null;
        Date endTime = null;

        if("all".equals(belong)){
            belong = null;
        }

        if(StringUtils.isNotBlank(registerTime) && !"all".equals(registerTime)){
            LocalDateTime now = LocalDateTime.now();
            switch (registerTime){
                case "today":
                    startTime = Date.from(now.with(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant());
                    endTime = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
                    break;
                case "current_week":
                    LocalDateTime weekStart = now.with(DayOfWeek.MONDAY).with(LocalTime.MIDNIGHT);
                    startTime = Date.from(weekStart.atZone(ZoneId.systemDefault()).toInstant());
                    endTime = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
                    break;
                case "current_month":
                    LocalDateTime monthStart = now.withDayOfMonth(1).with(LocalTime.MIDNIGHT);
                    startTime = Date.from(monthStart.atZone(ZoneId.systemDefault()).toInstant());
                    endTime = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
                    break;
            }
        }

        List<XrayUser> list = xrayUserService.selectXrayUserListV2(search, belong, startTime, endTime);
        return getDataTable(list);
    }

    @GetMapping("/detail/{id}")
    @ResponseBody
    public AjaxResult edit(@PathVariable("id") Long id)
    {
        XrayUser xrayUser = xrayUserService.selectXrayUserById(id);
        return AjaxResult.success(xrayUser);
    }

    /**
     * 修改保存用户管理
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XrayUser xrayUser)
    {
        // 将 Date 转换为当天结束时间 (23:59:59)
        Date expiration = xrayUser.getExpiration();

        // 获取日期部分并设置时间为 23:59:59
        LocalDateTime expirationEndOfDay = expiration.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .atTime(LocalTime.MAX);;

        // 转回 Date 类型
        Date endOfDayDate = Date.from(expirationEndOfDay.atZone(ZoneId.systemDefault()).toInstant());
        xrayUser.setExpiration(endOfDayDate);
        return toAjax(xrayUserService.updateXrayUser(xrayUser));
    }
}
