package com.ruoyi.system.service.impl;

import com.ruoyi.system.mapper.VpnLinesMapper;
import com.ruoyi.system.mapper.XrayTrafficCollectMapper;
import com.ruoyi.system.mapper.XrayUserMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component("xrayTrafficStatService")
public class XrayTrafficStatService {

    @Resource
    private XrayTrafficCollectMapper collectMapper;
    @Resource
    private VpnLinesMapper vpnLinesMapper;
    @Resource
    private XrayUserMapper xrayUserMapper;
    public void statAndSaveTraffic() {
        List<Map<String, Object>> inboundStats = collectMapper.statTrafficByClientIp();
        if(!CollectionUtils.isEmpty(inboundStats)){
            for (Map<String, Object> inboundStat : inboundStats) {
                vpnLinesMapper.updateTrafficByClientIp(inboundStat.get("client_ip").toString(), Integer.valueOf(inboundStat.get("used_traffic").toString()));
            }
        }

        List<Map<String, Object>> emailStats = collectMapper.statTrafficByEmail();
        if(!CollectionUtils.isEmpty(emailStats)) {
            for (Map<String, Object> emailStat : emailStats) {
                xrayUserMapper.updateTrafficByEmail(emailStat.get("email").toString(), Integer.valueOf(emailStat.get("used_traffic").toString()));
            }
        }
    }
}
