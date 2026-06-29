package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.XrayPaymentSubmitToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface XrayPaymentSubmitTokenMapper {

    int insertSubmitToken(XrayPaymentSubmitToken token);

    XrayPaymentSubmitToken selectByToken(@Param("token") String token);

    int useSubmitToken(@Param("token") String token, @Param("userId") Long userId, @Param("orderNo") String orderNo);

    int expireUnusedTokens();
}
