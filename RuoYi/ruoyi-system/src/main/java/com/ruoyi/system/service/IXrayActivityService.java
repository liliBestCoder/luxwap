package com.ruoyi.system.service;

import com.ruoyi.system.domain.XrayActivityConfig;
import com.ruoyi.system.domain.XrayActivityParticipants;

import java.util.List;
import java.util.Map;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
public interface IXrayActivityService 
{

    public List<XrayActivityConfig> selectXrayActivityConfigList(XrayActivityConfig xrayActivityConfig);


    public int updateXrayActivityConfig(XrayActivityConfig xrayActivityConfig);

    public void startActivity();

    public void joinActivity(String auditLink);

    public Map<String, Integer> getActivityParticipantsRankStats();

    public void rank(Long participantsId, Integer rank);

    public void saveSettings(Long id, Long participantsCnt, Long moderatorsCnt);

    public Map<String,Object> activityRankList();
}
