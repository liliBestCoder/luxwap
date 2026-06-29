package com.ruoyi.system.controller;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.XrayActivity;
import com.ruoyi.system.domain.XrayActivityConfig;
import com.ruoyi.system.mapper.XrayActivityParticipantsMapper;
import com.ruoyi.system.mapper.XrayActivityMapper;
import com.ruoyi.system.service.IXrayActivityService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
@Controller
@RequestMapping("/system/activity")
public class XrayActivityController extends BaseController
{

    @Autowired
    private IXrayActivityService xrayActivityService;
    @Autowired
    private XrayActivityMapper xrayActivityMapper;
    @Autowired
    private XrayActivityParticipantsMapper xrayActivityParticipantsMapper;

    @RequiresPermissions("system:activity:view")
    @GetMapping()
    public String activity()
    {
        return "pages/promotion";
    }

    @GetMapping("/settings")
    @ResponseBody
    public AjaxResult detail()
    {
        List<XrayActivityConfig> list = xrayActivityService.selectXrayActivityConfigList(new XrayActivityConfig());
        return CollectionUtils.isEmpty(list) ? success() : success(list.get(0));
    }

    @PostMapping("/save-settings")
    @ResponseBody
    public AjaxResult saveSettings(@RequestParam Long id,
                                   @RequestParam Long participantsCnt,
                                   @RequestParam Long moderatorsCnt)
    {
        xrayActivityService.saveSettings(id, participantsCnt, moderatorsCnt);
        return AjaxResult.success();
    }

    /**
     * 根据条件查询参与的用户列表
     *
     * @param userName 用户名
     * @param rank 名次
     * @param type 用户类型（归属）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 用户列表
     */
    @PostMapping("/participant-list")
    @ResponseBody
    public TableDataInfo getActivityParticipantsList(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) Integer rank,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate) {

        XrayActivity latestXrayActivity = xrayActivityMapper.selectLatestXrayActivity();
        if(latestXrayActivity == null){
            return getDataTable(Collections.emptyList());
        }

        startPage();
        return getDataTable(xrayActivityParticipantsMapper.selectActivityParticipantsByConditions(latestXrayActivity.getId(), userName, rank, type, startDate, endDate));
    }

    @Log(title = "活动管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(XrayActivityConfig xrayActivityConfig)
    {
        return toAjax(xrayActivityService.updateXrayActivityConfig(xrayActivityConfig));
    }


    @Log(title = "活动管理", businessType = BusinessType.UPDATE)
    @PostMapping("/startActivity")
    @ResponseBody
    public AjaxResult startActivity()
    {
        xrayActivityService.startActivity();
        return AjaxResult.success("开启活动成功!");
    }

    @GetMapping("/stats")
    @ResponseBody
    public AjaxResult getActivityParticipantsRankStats() {
        Map<String, Integer> rankStats = xrayActivityService.getActivityParticipantsRankStats();
        return AjaxResult.success(rankStats);
    }

    @PostMapping("/rank")
    @ResponseBody
    public AjaxResult rank(@RequestParam Long participantsId,
                           @RequestParam Integer rank) {
        if(participantsId == null){
            return AjaxResult.error("请选择正确的用户");
        }
        if(rank == null){
            return AjaxResult.error("请选择正确的排名");
        }
        xrayActivityService.rank(participantsId, rank);
        return AjaxResult.success();
    }
}
