package com.ruoyi.system.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.XrayActivityParticipants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
public interface XrayActivityParticipantsMapper
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public XrayActivityParticipants selectActivityParticipantsById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param xrayActivityParticipants 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<XrayActivityParticipants> selectActivityParticipantsList(XrayActivityParticipants xrayActivityParticipants);

    /**
     * 新增【请填写功能名称】
     * 
     * @param xrayActivityParticipants 【请填写功能名称】
     * @return 结果
     */
    public int insertActivityParticipants(XrayActivityParticipants xrayActivityParticipants);

    /**
     * 修改【请填写功能名称】
     * 
     * @param xrayActivityParticipants 【请填写功能名称】
     * @return 结果
     */
    public int updateActivityParticipants(XrayActivityParticipants xrayActivityParticipants);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteActivityParticipantsById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteActivityParticipantsByIds(String[] ids);

    /**
     * 根据条件查询参与的用户列表
     *
     * @param userName 用户名
     * @param rank 名次
     * @param type 用户类型（归属）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 参与的用户列表
     */
    public List<XrayActivityParticipants> selectActivityParticipantsByConditions(
            @Param("activityId") Long activityId,
            @Param("userName") String userName,
            @Param("rank") Integer rank,
            @Param("type") String type,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    /**
     * 获取活动参与人数统计，包括第一名、第二名、无效链接和总人数
     *
     * @param activityId 活动 ID
     * @return 统计结果
     */
    @Select({
            "SELECT " +
                    "   SUM(CASE WHEN `rank` = -1 THEN 1 ELSE 0 END) AS invalidLinkCount, " +
                    "   SUM(CASE WHEN `rank` = 1 THEN 1 ELSE 0 END) AS firstPlaceCount, " +
                    "   SUM(CASE WHEN `rank` = 2 THEN 1 ELSE 0 END) AS secondPlaceCount, " +
                    "   COUNT(*) AS totalCount " +
                    "FROM xray_activity_participants " +
                    "WHERE activity_id = #{activityId}"
    })
    public Map<String, Integer> selectRankStatsByActivityId(@Param("activityId") Long activityId);

    @Update("UPDATE xray_activity_participants SET `rank` = #{rank} WHERE `id` = #{participantsId} and `rank` = 0")
    int rank(@Param("participantsId") Long participantsId, @Param("rank") Integer rank);
}
