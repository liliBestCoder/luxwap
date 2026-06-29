package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.entity.SysUser;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
@Controller
@RequestMapping("/system/settings")
public class SettingsController extends BaseController
{
    @GetMapping()
    public String settings(ModelMap mmap)
    {
        SysUser user = getSysUser();
        mmap.put("loginName", user.getLoginName());
        return "pages/settings";
    }

}
