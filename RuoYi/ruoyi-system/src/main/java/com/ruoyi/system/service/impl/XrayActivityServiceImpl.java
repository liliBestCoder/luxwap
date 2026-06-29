package com.ruoyi.system.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.ruoyi.common.threadlocal.XrayThreadLocal;
import com.ruoyi.system.domain.XrayActivityParticipants;
import com.ruoyi.system.domain.XrayActivityConfig;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.mapper.XrayActivityParticipantsMapper;
import com.ruoyi.system.mapper.XrayActivityConfigMapper;
import com.ruoyi.system.mapper.XrayUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.XrayActivityMapper;
import com.ruoyi.system.domain.XrayActivity;
import com.ruoyi.system.service.IXrayActivityService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
@Service
public class XrayActivityServiceImpl implements IXrayActivityService 
{
    @Autowired
    private XrayActivityMapper xrayActivityMapper;
    @Autowired
    private XrayActivityConfigMapper xrayActivityConfigMapper;
    @Autowired
    private XrayUserMapper xrayUserMapper;
    @Autowired
    private XrayActivityParticipantsMapper xrayActivityParticipantsMapper;
    @Override
    public List<XrayActivityConfig> selectXrayActivityConfigList(XrayActivityConfig xrayActivityConfig)
    {
        return xrayActivityConfigMapper.selectXrayActivityConfigList(xrayActivityConfig);
    }


    @Override
    public int updateXrayActivityConfig(XrayActivityConfig xrayActivityConfig)
    {
        List<XrayActivityConfig> xrayActivityConfigList = selectXrayActivityConfigList(new XrayActivityConfig());
        if(CollectionUtils.isEmpty(xrayActivityConfigList)){
            xrayActivityConfig.setId(null);
            xrayActivityConfigMapper.insertXrayActivityConfig(xrayActivityConfig);
            return 1;
        }else {
            XrayActivityConfig xrayActivityConfigExist = xrayActivityConfigList.get(0);
            boolean start = xrayActivityConfigExist.getStatus() == 1L;
            xrayActivityConfig.setId(xrayActivityConfigExist.getId());
            xrayActivityConfig.setStatus(null);

            if(start){
                xrayActivityConfig.setParticipantsCnt(null);
                XrayActivity latestXrayActivity = xrayActivityMapper.selectLatestXrayActivity();
                XrayActivity latestXrayActivityUpdate = new XrayActivity();
                latestXrayActivityUpdate.setId(latestXrayActivity.getId());
                latestXrayActivityUpdate.setModeratorsCnt(xrayActivityConfig.getModeratorsCnt());
                xrayActivityMapper.updateXrayActivity(latestXrayActivityUpdate);
            }

            return xrayActivityConfigMapper.updateXrayActivityConfig(xrayActivityConfig);
        }
    }

    @Transactional
    public void startActivity(){
        List<XrayActivityConfig> xrayActivityConfigList = selectXrayActivityConfigList(new XrayActivityConfig());
        if(CollectionUtils.isEmpty(xrayActivityConfigList)){
            throw new RuntimeException("暂未有活动, 敬请期待!");
        }

        XrayActivityConfig xrayActivityConfig = xrayActivityConfigList.get(0);
        boolean start = xrayActivityConfig.getStatus() == 1L;
        if(start){
            throw new RuntimeException("活动已开始!");
        }

        XrayActivityConfig xrayActivityConfigUpdate = new XrayActivityConfig();
        xrayActivityConfigUpdate.setId(xrayActivityConfig.getId());
        xrayActivityConfigUpdate.setStatus(1L);

        xrayActivityConfigMapper.updateXrayActivityConfig(xrayActivityConfigUpdate);

        XrayActivity xrayActivity = new XrayActivity();
        xrayActivity.setActivityConfigId(xrayActivityConfig.getId());
        xrayActivity.setParticipantsCnt(xrayActivityConfig.getParticipantsCnt());
        xrayActivity.setModeratorsCnt(xrayActivityConfig.getModeratorsCnt());
        xrayActivityMapper.insertXrayActivity(xrayActivity);
    }


    public void joinActivity(String auditLink) {
        Long userId = XrayThreadLocal.getUid();

        XrayUser xrayUser = xrayUserMapper.selectXrayUserById(userId);
        String userEmail = xrayUser.getEmail();
        String username = xrayUser.getUsername();
        // 设置默认的会员状态和有效期
        long member = xrayUser != null ? 1l : 0l;// 假设所有参与的用户都是会员
        Date expiration = xrayUser.getExpiration();

        XrayActivity latestXrayActivity = xrayActivityMapper.selectLatestXrayActivity();
        if(latestXrayActivity == null){
            throw new RuntimeException("暂未有活动, 敬请期待!");
        }

        List<XrayActivityConfig> xrayActivityConfigs = xrayActivityConfigMapper.selectXrayActivityConfigList(new XrayActivityConfig());

        if(xrayActivityConfigs.get(0).getStatus() == 0){
            throw new RuntimeException("暂未有活动, 敬请期待!");
        }

        Long activityId = latestXrayActivity.getId();

        XrayActivityParticipants queryCondition = new XrayActivityParticipants();
        queryCondition.setActivityId(activityId);
        queryCondition.setUserId(userId);

        List<XrayActivityParticipants> xrayActivityParticipantsList = xrayActivityParticipantsMapper.selectActivityParticipantsList(queryCondition);
        if(!CollectionUtils.isEmpty(xrayActivityParticipantsList)){
            throw new RuntimeException("您已参与过活动!");
        }


        Long participantsCnt = latestXrayActivity.getParticipantsCnt();
        Long moderatorsCnt = latestXrayActivity.getModeratorsCnt();

        if(participantsCnt - 1 >= moderatorsCnt){
            xrayActivityMapper.subParticipantsCnt(latestXrayActivity.getId());
            // 创建活动参与对象
            XrayActivityParticipants xrayActivityParticipants = new XrayActivityParticipants();
            xrayActivityParticipants.setUserId(userId);
            xrayActivityParticipants.setRegistrationTime(xrayUser.getCreateTime());
            xrayActivityParticipants.setUserEmail(userEmail);
            xrayActivityParticipants.setUserName(username);
            xrayActivityParticipants.setMember(member);
            xrayActivityParticipants.setExpiration(expiration);
            xrayActivityParticipants.setRank(0L);
            xrayActivityParticipants.setType(xrayUser.getType());
            xrayActivityParticipants.setActivityId(activityId);
            xrayActivityParticipants.setAuditLink(auditLink);

            xrayActivityParticipantsMapper.insertActivityParticipants(xrayActivityParticipants);
        }

        if(moderatorsCnt >= participantsCnt - 1){
            // 结束活动
            XrayActivityConfig updateXrayActivityConfig = new XrayActivityConfig();
            updateXrayActivityConfig.setId(xrayActivityConfigs.get(0).getId());
            updateXrayActivityConfig.setStatus(0L);
            xrayActivityConfigMapper.updateXrayActivityConfig(updateXrayActivityConfig);
        }
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


    /**
     * 查询活动参与人数统计，包括第一名、第二名、无效链接个数及总人数
     *
     * @return 统计结果
     */
    public Map<String, Integer> getActivityParticipantsRankStats() {
        XrayActivity latestXrayActivity = xrayActivityMapper.selectLatestXrayActivity();
        if(latestXrayActivity == null){
            return new HashMap<String, Integer>(){{
                put("firstPlaceCount", 0);
                put("secondPlaceCount", 0);
                put("invalidLinkCount", 0);
                put("totalCount", 0);
            }};
        }
        // 查询第一名、第二名、无效链接和总人数
        return xrayActivityParticipantsMapper.selectRankStatsByActivityId(latestXrayActivity.getId());
    }

    public void rank(Long participantsId, Integer rank) {
        XrayActivityParticipants xrayActivityParticipants = xrayActivityParticipantsMapper.selectActivityParticipantsById(participantsId);
        if(xrayActivityParticipants == null){
            throw new RuntimeException("请选择正确的用户");
        }
        int rows = xrayActivityParticipantsMapper.rank(participantsId, rank);
        if(rows <= 0){
            throw new RuntimeException("用户已排名");
        }
    }

    public void saveSettings(Long id, Long participantsCnt, Long moderatorsCnt){
        if(moderatorsCnt > participantsCnt){
            throw new RuntimeException("调节数量不能大于参与者数量");
        }

        XrayActivityConfig xrayActivityConfig = xrayActivityConfigMapper.selectXrayActivityConfigById(id);
        if(xrayActivityConfig == null){
            throw new RuntimeException("活动配置不存在");
        }

        boolean start = xrayActivityConfig.getStatus() == 1L;

        XrayActivityConfig update = new XrayActivityConfig();
        update.setId(id);

        update.setModeratorsCnt(moderatorsCnt);
        if (!start){
            update.setParticipantsCnt(participantsCnt);
        }

        xrayActivityConfigMapper.updateXrayActivityConfig(update);
    }

    public Map<String,Object> activityRankList(){
        XrayActivity xrayActivity = xrayActivityMapper.selectLatestXrayActivity();
        if(xrayActivity == null){
            return new HashMap<>();
        }
        XrayActivityParticipants query = new XrayActivityParticipants();
        query.setActivityId(xrayActivity.getId());

        List<XrayActivityParticipants> xrayActivityParticipants = xrayActivityParticipantsMapper.selectActivityParticipantsList(query);
        Map<String,Object> result = new HashMap<>();

        XrayActivityConfig xrayActivityConfig = xrayActivityConfigMapper.selectXrayActivityConfigById(xrayActivity.getActivityConfigId());

        result.put("total", xrayActivityConfig.getParticipantsCnt());
        result.put("rest", xrayActivity.getParticipantsCnt() - xrayActivity.getModeratorsCnt() >= 0 ? xrayActivity.getParticipantsCnt() - xrayActivity.getModeratorsCnt() : 0);
        result.put("rankList", xrayActivityParticipants.stream().filter(p -> p.getRank() > 0).sorted(Comparator.comparingLong(XrayActivityParticipants::getRank)).collect(Collectors.toList()));

        return result;
    }
}
