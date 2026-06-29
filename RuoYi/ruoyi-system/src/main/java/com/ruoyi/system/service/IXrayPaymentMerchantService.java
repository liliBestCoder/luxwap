package com.ruoyi.system.service;

import com.ruoyi.system.domain.XrayPaymentMerchant;

import java.util.List;

public interface IXrayPaymentMerchantService {
    void saveOrUpdate(XrayPaymentMerchant merchant) throws IllegalArgumentException;
    XrayPaymentMerchant selectByType(String type);
    List<XrayPaymentMerchant> selectAll();
    List<XrayPaymentMerchant> selectEnabled();
}
