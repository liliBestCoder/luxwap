package com.ruoyi.web.ws.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.system.domain.XrayTrafficCollect;
import com.ruoyi.system.mapper.VpnLinesMapper;
import com.ruoyi.system.mapper.XrayTrafficCollectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MsgService {

    @Autowired
    private XrayTrafficCollectMapper xrayTrafficCollectMapper;
    @Autowired
    private VpnLinesMapper vpnLinesMapper;

    public void saveTrafficCollectList(String clientIp, JSONObject msg){
        JSONArray collectList = msg.getJSONArray("data");
        Object timeJson = msg.get("time"); // 假设你用的是 JSONObject

        double seconds = ((Number) timeJson).doubleValue();
        Date time = Date.from(Instant.ofEpochSecond((long) seconds, (long) ((seconds % 1) * 1_000_000_000)));

        int size = collectList.size();
        List<XrayTrafficCollect> xrayTrafficCollectList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            JSONObject collect = collectList.getJSONObject(i);
            Long value = collect.getLong("delta");
            if(value == null){
                continue;
            }
            XrayTrafficCollect xrayTrafficCollect = new XrayTrafficCollect();
            xrayTrafficCollect.setClientIp(clientIp);
            String name = collect.getString("name");
            String[] nameSplits = name.split(">>>");
            xrayTrafficCollect.setLevel(nameSplits[0]);
            xrayTrafficCollect.setEmail(nameSplits[1]);
            xrayTrafficCollect.setType(nameSplits[3]);
            xrayTrafficCollect.setVal(value);
            xrayTrafficCollect.setTime(time);
            xrayTrafficCollectList.add(xrayTrafficCollect);
        }

        if(CollectionUtils.isEmpty(xrayTrafficCollectList)){
            return;
        }
        xrayTrafficCollectMapper.saveXrayTrafficCollectList(xrayTrafficCollectList);
    }

    public void updateXrayStatus(String clientIp, JSONObject msg) {
        JSONObject statusObj = msg.getJSONObject("data");
        String status = statusObj.getString("status");
        vpnLinesMapper.updateStatusByClientIp(clientIp, status);
    }
}
