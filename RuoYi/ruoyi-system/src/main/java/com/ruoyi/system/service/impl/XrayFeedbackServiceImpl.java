package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.XrayFeedback;
import com.ruoyi.system.mapper.XrayFeedbackMapper;
import com.ruoyi.system.service.IXrayFeedbackService;

/**
 * 用户意见与问题反馈 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class XrayFeedbackServiceImpl implements IXrayFeedbackService
{
    @Autowired
    private XrayFeedbackMapper xrayFeedbackMapper;

    @Override
    public int submitFeedback(XrayFeedback feedback)
    {
        if (feedback.getStatus() == null) {
            feedback.setStatus(0);
        }
        if (feedback.getDelFlag() == null) {
            feedback.setDelFlag("0");
        }
        return xrayFeedbackMapper.insertFeedback(feedback);
    }

    @Override
    public List<XrayFeedback> selectFeedbackList(XrayFeedback feedback)
    {
        return xrayFeedbackMapper.selectFeedbackList(feedback);
    }

    @Override
    public XrayFeedback selectFeedbackById(Long id)
    {
        return xrayFeedbackMapper.selectFeedbackById(id);
    }

    @Override
    public int updateFeedback(XrayFeedback feedback)
    {
        return xrayFeedbackMapper.updateFeedback(feedback);
    }
}
