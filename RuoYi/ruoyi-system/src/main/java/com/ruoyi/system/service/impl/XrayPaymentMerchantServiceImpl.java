package com.ruoyi.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruoyi.system.domain.XrayPaymentMerchant;
import com.ruoyi.system.mapper.XrayPaymentMerchantMapper;
import com.ruoyi.system.service.IXrayPaymentMerchantService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class XrayPaymentMerchantServiceImpl implements IXrayPaymentMerchantService {

    @Resource
    private XrayPaymentMerchantMapper mapper;

    @Override
    public void saveOrUpdate(XrayPaymentMerchant merchant) throws IllegalArgumentException {
        if (!StringUtils.hasText(merchant.getType()) || !StringUtils.hasText(merchant.getConfig())) {
            throw new IllegalArgumentException("payType and config cannot be empty");
        }

        Map<String, Object> configMap;
        try {
            configMap = JSON.parseObject(merchant.getConfig(), Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("config JSON format is invalid");
        }

        switch (merchant.getType()) {
            case "alipay":
                require(configMap, "appId", "privateKey", "alipayPublicKey", "notifyUrl", "format", "charset", "signType");
                break;
            case "wechat":
                require(configMap, "appId", "mchId", "apiKey", "notifyUrl");
                break;
            case "stripe":
                require(configMap, "secretKey", "publishableKey");
                break;
            case "paypal":
                require(configMap, "clientId", "clientSecret");
                break;
            case "circle":
                require(configMap, "apiKey", "accountId");
                break;
            default:
                throw new IllegalArgumentException("unknown payment type");
        }

        if (merchant.getStatus() == null) {
            merchant.setStatus(1);
        }

        XrayPaymentMerchant exist = mapper.selectByType(merchant.getType());
        if (exist != null) {
            merchant.setId(exist.getId());
            mapper.update(merchant);
        } else {
            mapper.insert(merchant);
        }
    }

    private void require(Map<String, Object> configMap, String... keys) {
        for (String key : keys) {
            Object value = configMap.get(key);
            if (value == null || !StringUtils.hasText(value.toString())) {
                throw new IllegalArgumentException("payment config missing required field: " + key);
            }
        }
    }

    @Override
    public XrayPaymentMerchant selectByType(String type) {
        return mapper.selectByType(type);
    }

    @Override
    public List<XrayPaymentMerchant> selectAll() {
        return mapper.selectAll();
    }

    @Override
    public List<XrayPaymentMerchant> selectEnabled() {
        return mapper.selectEnabled();
    }
}
