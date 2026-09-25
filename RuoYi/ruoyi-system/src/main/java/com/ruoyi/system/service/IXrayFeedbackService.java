package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.XrayFeedback;

/**
 * 用户意见与问题反馈 服务层
 * 
 * @author ruoyi
 */
public interface IXrayFeedbackService
{
    /**
     * 客户端提交用户反馈
     * 
     * @param feedback 用户反馈信息
     * @return 结果
     */
    public int submitFeedback(XrayFeedback feedback);

    /**
     * 查询用户反馈列表
     * 
     * @param feedback 查询条件
     * @return 列表
     */
    public List<XrayFeedback> selectFeedbackList(XrayFeedback feedback);

    /**
     * 根据ID查询反馈
     * 
     * @param id 主键ID
     * @return 反馈详情
     */
    public XrayFeedback selectFeedbackById(Long id);

    /**
     * 回复或更新反馈
     * 
     * @param feedback 反馈信息
     * @return 结果
     */
    public int updateFeedback(XrayFeedback feedback);
}
