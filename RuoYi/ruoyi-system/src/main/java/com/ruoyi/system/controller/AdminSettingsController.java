package com.ruoyi.system.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.system.domain.XrayInnerUser;
import com.ruoyi.system.domain.XrayPacket;
import com.ruoyi.system.domain.XrayPaymentMerchant;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.IXrayInnerUserService;
import com.ruoyi.system.service.IXrayPacketService;
import com.ruoyi.system.service.IXrayPaymentMerchantService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
@Controller
@RequestMapping("/system/adminsettings")
public class AdminSettingsController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;
    @Autowired
    private IXrayInnerUserService xrayInnerUserService;
    @Autowired
    private IXrayPaymentMerchantService merchantService;
    @Autowired
    private IXrayPacketService packetService;

    @RequiresPermissions("system:adminsettings:view")
    @GetMapping()
    public String adminsettings(ModelMap mmap, HttpServletRequest request)
    {
        SysUser user = getSysUser();
        // 根据用户id取出菜单
        List<SysMenu> menus = menuService.adminSettingsPermsList(user);

        Set<String> userPerms = menus.stream()
                .map(SysMenu::getPerms)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SysMenu> allMenus = menuService.selectAllLuxwapMenuList();
        allMenus.sort(Comparator.comparing(SysMenu::getMenuId));

        List<String> resellerMenus = menuService.selectLuxwapPermsListByRoleKey("reseller");
        List<String> adminSlaveMenus = menuService.selectLuxwapPermsListByRoleKey("admin-slave");
        List<String> supportMenus = menuService.selectLuxwapPermsListByRoleKey("support");
        List<String> adminMenus = menuService.selectLuxwapPermsListByRoleKey("admin");
        if(CollectionUtils.isEmpty(adminMenus)){
            adminMenus = allMenus.stream().map(SysMenu::getPerms).collect(Collectors.toList());
        }

        mmap.put("resellerPerms", resellerMenus.stream().collect(Collectors.toMap(Function.identity(), p -> true)));
        mmap.put("superAdminPerms", adminMenus.stream().collect(Collectors.toMap(Function.identity(), p -> true)));
        mmap.put("adminSlavePerms", adminSlaveMenus.stream().collect(Collectors.toMap(Function.identity(), p -> true)));
        mmap.put("supportPerms", supportMenus.stream().collect(Collectors.toMap(Function.identity(), p -> true)));

        mmap.put("hasPaymentConfig", userPerms.contains("system:adminsettings:payment"));
        mmap.put("hasUserManager", userPerms.contains("system:adminsettings:users"));
        mmap.put("hasLinesManager", userPerms.contains("system:adminsettings:lines"));
        mmap.put("hasBoardManager", userPerms.contains("system:adminsettings:board"));
        mmap.put("hasActivityManager", userPerms.contains("system:adminsettings:activity"));
        mmap.put("hasDistributorsManager", userPerms.contains("system:adminsettings:distributors"));
        mmap.put("hasPermsManager", userPerms.contains("system:adminsettings:perms"));
        return "pages/adminsettings";
    }

    @GetMapping("/partials")
    public String partials()
    {
        return "pages/partials/admin-modals";
    }


    public static class SavePermsReq{
        private String roleKey;
        private String perms;

        public String getRoleKey() {
            return roleKey;
        }

        public void setRoleKey(String roleKey) {
            this.roleKey = roleKey;
        }

        public String getPerms() {
            return perms;
        }

        public void setPerms(String perms) {
            this.perms = perms;
        }
    }
    @RequiresPermissions("system:adminsettings:view")
    @PostMapping("/save-perms")
    @ResponseBody
    public AjaxResult savePerms(@RequestBody List<SavePermsReq> savePermsReqList)
    {
        menuService.savePerms(savePermsReqList);
        return AjaxResult.success();
    }


    /**
     * 查询内部用户列表
     */
    @RequiresPermissions("system:adminsettings:view")
    @PostMapping("/list-inner-users")
    @ResponseBody
    public TableDataInfo list(@RequestParam String userName,
                              @RequestParam String type) {
        startPage();
        XrayInnerUser query = new XrayInnerUser();
        query.setUserName(userName);
        query.setType(type);
        List<XrayInnerUser> list = xrayInnerUserService.selectXrayInnerUserLikeList(query);
        return getDataTable(list);
    }

    @RequiresPermissions("system:adminsettings:view")
    @PostMapping("/save-inner-user")
    @ResponseBody
    public AjaxResult add(@RequestBody XrayInnerUser xrayInnerUser) {
        SysUser sysUser = ShiroUtils.getSysUser();
        return toAjax(xrayInnerUserService.insertXrayInnerUser(xrayInnerUser, sysUser.getLoginName()));
    }

    @RequiresPermissions("system:adminsettings:view")
    @DeleteMapping("/del-inner-user/{id}")
    @ResponseBody
    public AjaxResult remove(@PathVariable Long id) {
        return toAjax(xrayInnerUserService.deleteXrayInnerUserById(id));
    }

    @RequiresPermissions("system:adminsettings:view")
    @PostMapping("/disable-inner-user/{id}")
    @ResponseBody
    public AjaxResult disable(@PathVariable Long id) {
        return toAjax(xrayInnerUserService.diableXrayInnerUserById(id));
    }

    @RequiresPermissions("system:adminsettings:view")
    @PostMapping("/payment/save")
    @ResponseBody
    public AjaxResult save(XrayPaymentMerchant merchant) {
        try {
            merchantService.saveOrUpdate(merchant);
            return AjaxResult.success("保存成功");
        } catch (IllegalArgumentException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @RequiresPermissions("system:adminsettings:view")
    @GetMapping("/payment/get/{type}")
    @ResponseBody
    public AjaxResult getPaymentConfig(@PathVariable String type) {
        XrayPaymentMerchant merchant = merchantService.selectByType(type);
        return AjaxResult.success(merchant);
    }

    @RequiresPermissions("system:adminsettings:view")
    @GetMapping("/packet/list")
    @ResponseBody
    public AjaxResult packetList() {
        return AjaxResult.success(packetService.getPacketList());
    }

    @RequiresPermissions("system:adminsettings:view")
    @PostMapping("/packet/save")
    @ResponseBody
    public AjaxResult savePacket(@RequestBody XrayPacket packet) {
        if (packet.getName() == null || packet.getName().trim().isEmpty()) {
            return AjaxResult.error("套餐名称不能为空");
        }
        if (packet.getDurationMonths() == null || packet.getDurationMonths() <= 0) {
            return AjaxResult.error("套餐月份必须大于 0");
        }
        if (packet.getBonusMonths() == null) {
            packet.setBonusMonths(0);
        }
        if (packet.getPrice() == null || packet.getPrice().signum() < 0) {
            return AjaxResult.error("套餐价格不能小于 0");
        }
        if (packet.getPricePerMonth() == null || packet.getPricePerMonth().signum() < 0) {
            return AjaxResult.error("月均价格不能小于 0");
        }
        return toAjax(packetService.savePacket(packet));
    }

    @RequiresPermissions("system:adminsettings:view")
    @DeleteMapping("/packet/delete/{id}")
    @ResponseBody
    public AjaxResult deletePacket(@PathVariable Long id) {
        return toAjax(packetService.deletePacketById(id));
    }
}
