package com.ruoyi.system.service;

import java.util.Date;
import com.ruoyi.system.domain.XrayPaymentSubmitToken;

public interface IXrayPaymentSubmitTokenService {

    void createToken(String token, Long userId, Date expiredAt);

    XrayPaymentSubmitToken getByToken(String token);
}
