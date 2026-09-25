package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.XrayFeedback;

/**
 * 用户意见与问题反馈 Mapper 接口
 * 
 * @author ruoyi
 */
public interface XrayFeedbackMapper
{
    /**
     * 新增用户反馈
     * 
     * @param feedback 用户反馈
     * @return 结果
     */
    public int insertFeedback(XrayFeedback feedback);

    /**
     * 查询用户反馈列表
     * 
     * @param feedback 用户反馈
     * @return 用户反馈集合
     */
    public List<XrayFeedback> selectFeedbackList(XrayFeedback feedback);

    /**
     * 根据主键查询用户反馈详情
     * 
     * @param id 用户反馈主键
     * @return 用户反馈
     */
    public XrayFeedback selectFeedbackById(Long id);

    /**
     * 修改用户反馈状态或回复
     * 
     * @param feedback 用户反馈
     * @return 结果
     */
    public int updateFeedback(XrayFeedback feedback);
}
