package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.XrayPaymentMerchant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XrayPaymentMerchantMapper {

    // 查询所有商户
    List<XrayPaymentMerchant> selectAll();
    List<XrayPaymentMerchant> selectEnabled();

    XrayPaymentMerchant selectByType(@Param("type") String payType);

    int insert(XrayPaymentMerchant merchant);
    int update(XrayPaymentMerchant merchant);
}

