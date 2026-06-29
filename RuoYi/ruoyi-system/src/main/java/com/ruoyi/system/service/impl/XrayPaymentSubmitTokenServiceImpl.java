package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.XrayPaymentSubmitToken;
import com.ruoyi.system.mapper.XrayPaymentSubmitTokenMapper;
import com.ruoyi.system.service.IXrayPaymentSubmitTokenService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class XrayPaymentSubmitTokenServiceImpl implements IXrayPaymentSubmitTokenService {

    @Resource
    private XrayPaymentSubmitTokenMapper submitTokenMapper;

    @Override
    public void createToken(String token, Long userId, Date expiredAt) {
        XrayPaymentSubmitToken submitToken = new XrayPaymentSubmitToken();
        submitToken.setToken(token);
        submitToken.setUserId(userId);
        submitToken.setStatus(0);
        submitToken.setCreatedAt(new Date());
        submitToken.setExpiredAt(expiredAt);
        submitTokenMapper.insertSubmitToken(submitToken);
    }

    @Override
    public XrayPaymentSubmitToken getByToken(String token) {
        return submitTokenMapper.selectByToken(token);
    }
}
