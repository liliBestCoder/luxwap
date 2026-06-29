package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.XrayActivity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
public interface XrayActivityMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    public XrayActivity selectXrayActivityById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param xrayActivity 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<XrayActivity> selectXrayActivityList(XrayActivity xrayActivity);

    /**
     * 新增【请填写功能名称】
     * 
     * @param xrayActivity 【请填写功能名称】
     * @return 结果
     */
    public int insertXrayActivity(XrayActivity xrayActivity);

    /**
     * 修改【请填写功能名称】
     * 
     * @param xrayActivity 【请填写功能名称】
     * @return 结果
     */
    public int updateXrayActivity(XrayActivity xrayActivity);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    public int deleteXrayActivityById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteXrayActivityByIds(String[] ids);

    @Select("select id, activity_config_id as activityConfigId, participants_cnt as participantsCnt, moderators_cnt as moderatorsCnt, created_at as createdAt, updated_at as  updatedAt from xray_activity order by id desc LIMIT  1")
    public XrayActivity selectLatestXrayActivity();

    @Update("update xray_activity set participants_cnt = participants_cnt - 1 where id = #{id}")
    public int subParticipantsCnt(@Param("id") Long id);
}
