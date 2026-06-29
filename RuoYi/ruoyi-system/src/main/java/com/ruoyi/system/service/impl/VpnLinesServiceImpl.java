package com.ruoyi.system.service.impl;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.ruoyi.common.utils.StringUtils;

import com.ruoyi.system.domain.VpnLinesConfig;
import com.ruoyi.system.domain.VpnLinesExcel;
import com.ruoyi.system.mapper.VpnLinesConfigMapper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.VpnLinesMapper;
import com.ruoyi.system.domain.VpnLines;
import com.ruoyi.system.service.IVpnLinesService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.util.CollectionUtils;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.math.ec.rfc7748.X25519;


/**
 * VPN线路信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-09
 */
@Service
public class VpnLinesServiceImpl implements IVpnLinesService, InitializingBean, DisposableBean
{
    @Autowired
    private VpnLinesMapper vpnLinesMapper;
    @Autowired
    private VpnLinesConfigMapper vpnLineConfigMapper;

    private ExecutorService executorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = Executors.newFixedThreadPool(5);
    }


    @Override
    public void destroy(){
        executorService.shutdown();
    }

    /**
     * 查询VPN线路信息
     * 
     * @param id VPN线路信息主键
     * @return VPN线路信息
     */
    @Override
    public VpnLines selectVpnLinesById(Long id)
    {
        return vpnLinesMapper.selectVpnLinesById(id);
    }

    /**
     * 查询VPN线路信息列表
     * 
     * @param vpnLines VPN线路信息
     * @return VPN线路信息
     */
    @Override
    public List<VpnLines> selectVpnLinesList(VpnLines vpnLines)
    {
        return vpnLinesMapper.selectVpnLinesList(vpnLines);
    }

    /**
     * 新增VPN线路信息
     * 
     * @param vpnLines VPN线路信息
     * @return 结果
     */
    @Override
    public int insertVpnLines(VpnLines vpnLines)
    {
        return vpnLinesMapper.insertVpnLines(vpnLines);
    }

    /**
     * 修改VPN线路信息
     * 
     * @param vpnLines VPN线路信息
     * @return 结果
     */
    @Override
    public int updateVpnLines(VpnLines vpnLines)
    {
        return vpnLinesMapper.updateVpnLines(vpnLines);
    }

    /**
     * 批量删除VPN线路信息
     * 
     * @param ids 需要删除的VPN线路信息主键
     * @return 结果
     */
    @Override
    public int deleteVpnLinesByIds(String ids)
    {
        return vpnLinesMapper.deleteVpnLinesByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除VPN线路信息信息
     * 
     * @param id VPN线路信息主键
     * @return 结果
     */
    @Override
    public int deleteVpnLinesById(String id)
    {
        return vpnLinesMapper.deleteVpnLinesById(id);
    }

    @Override
    public String batchImportVpnLineList(List<VpnLinesExcel> vpnLinesExcelList) {
        for (VpnLinesExcel data : vpnLinesExcelList) {
            // 检查数据库中是否已存在该线路
            VpnLines queryCondition = new VpnLines();
            queryCondition.setName(data.getName());
            List<VpnLines> existingLineList = vpnLinesMapper.selectVpnLinesList(queryCondition);
            if (!CollectionUtils.isEmpty(existingLineList)) {
                VpnLines existingLine = existingLineList.get(0);
                VpnLines updateLine = new VpnLines();
                updateLine.setId(existingLine.getId());
                updateLine.setRegion(data.getRegion());
                updateLine.setType(data.getType());
                updateLine.setBandwidth(data.getBandwidth());
                updateLine.setTotalTraffic(data.getTotalTraffic());
                updateLine.setPingOffset(data.getPingOffset());
                updateLine.setIp(data.getIp());
                updateLine.setPort(data.getPort());
                updateLine.setKeyword(data.getKeyword());
                updateLine.setMaxConnectionCnt(data.getMaxConnectionCnt());
                vpnLinesMapper.updateVpnLines(updateLine);

                // 更新配置表
                VpnLinesConfig config = new VpnLinesConfig();
                config.setVpnLineId(existingLine.getId());
                config.setConfigJson(data.getConfigJson());
                vpnLineConfigMapper.updateVpnLinesConfigByVpnLineId(config);
            } else {
                // 如果不存在，插入新线路记录
                VpnLines vpnLine = new VpnLines();
                vpnLine.setRegion(data.getRegion());
                vpnLine.setType(data.getType());
                vpnLine.setBandwidth(data.getBandwidth());
                vpnLine.setName(data.getName());
                vpnLine.setTotalTraffic(data.getTotalTraffic());
                vpnLine.setRemainingTraffic(data.getTotalTraffic()); // 默认剩余流量等于总流量
                vpnLine.setConnectionCnt(0); // 默认连接数为0
                vpnLine.setPingDelay(0); // 默认ping延迟为0
                vpnLine.setPingOffset(data.getPingOffset());
                vpnLine.setStatus("offline"); // 默认状态为offline
                vpnLine.setProtocol("vless"); // 默认协议为vless
                vpnLine.setIp(data.getIp());
                vpnLine.setPort(data.getPort());
                vpnLine.setKeyword(data.getKeyword());
                vpnLine.setMaxConnectionCnt(data.getMaxConnectionCnt());
                vpnLinesMapper.insertVpnLines(vpnLine);

                // 插入 config 配置
                VpnLinesConfig config = new VpnLinesConfig();
                config.setVpnLineId(vpnLine.getId());
                config.setConfigJson(data.getConfigJson());
                vpnLineConfigMapper.insertVpnLinesConfig(config);
            }
        }
        return "success";
    }

    public List<String> generateVlessLinkList() {
        List<String> vlessLinkList = new ArrayList<>();

        // 查询所有线路
        VpnLines queryCondition = new VpnLines();
        List<VpnLines> vpnLinesList = vpnLinesMapper.selectVpnLinesList(queryCondition);

        if (CollectionUtils.isEmpty(vpnLinesList)) {
            return vlessLinkList;
        }

        List<VpnLinesConfig> vpnLinesConfigList = vpnLineConfigMapper.selectVpnLinesConfigList(new VpnLinesConfig());

        Map<Long, VpnLinesConfig> vpnLinesConfigMappings = vpnLinesConfigList.stream().collect(Collectors.toMap(VpnLinesConfig::getVpnLineId, Function.identity()));

        for (VpnLines vpnLine : vpnLinesList) {
            try {
                // 获取线路配置
                VpnLinesConfig config = vpnLinesConfigMappings.get(vpnLine.getId());
                if (config == null) {
                    continue;
                }

                // 解析配置JSON
                JSONObject configJson = JSON.parseObject(config.getConfigJson());

                // 提取inbounds信息
                JSONArray inbounds = configJson.getJSONArray("inbounds");
                if (inbounds == null || inbounds.isEmpty()) {
                    continue;
                }

                // 直接取第一个inbound（按约定就是vless协议）
                JSONObject vlessInbound = inbounds.getJSONObject(0);

                // 提取必要信息
                JSONObject inboundsSettings = vlessInbound.getJSONObject("settings");
                if (inboundsSettings == null) {
                    continue;
                }

                JSONArray clients = inboundsSettings.getJSONArray("clients");
                if (clients == null || clients.isEmpty()) {
                    continue;
                }

                JSONObject client = clients.getJSONObject(0); // 取第一个客户端
                String flow = client.getString("flow");

                JSONObject streamSettings = vlessInbound.getJSONObject("streamSettings");
                if (streamSettings == null) {
                    continue;
                }

                String network = streamSettings.getString("network");
                String security = streamSettings.getString("security");
                String keyword = vpnLine.getKeyword();

                // 构建基础URL部分
                String userInfo = "${uuid}";
                String hostInfo = vpnLine.getIp() + ":" + vpnLine.getPort();

                // 构建查询参数
                StringBuilder queryParams = new StringBuilder();
                queryParams.append("encryption=none");

                if (StringUtils.isNotEmpty(flow)) {
                    queryParams.append("&flow=").append(flow);
                }

                if (StringUtils.isNotEmpty(security)) {
                    queryParams.append("&security=").append(security);
                }

                // 提取reality设置
                if ("reality".equals(security)) {
                    JSONObject realitySettings = streamSettings.getJSONObject("realitySettings");
                    if (realitySettings != null) {
                        JSONArray serverNames = realitySettings.getJSONArray("serverNames");
                        if (serverNames != null && !serverNames.isEmpty()) {
                            queryParams.append("&sni=").append(serverNames.getString(0));
                        }

                        JSONObject realitySettingsSettings = realitySettings.getJSONObject("settings");
                        if(realitySettingsSettings != null && !realitySettingsSettings.isEmpty()){
                            queryParams.append("&fp=").append(realitySettingsSettings.getString("fingerprint")); // 默认指纹
                        }else{
                            queryParams.append("&fp=chrome"); // 默认指纹
                        }

                        String privateKey = realitySettings.getString("privateKey");
                        if (StringUtils.isNotEmpty(privateKey)) {
                            queryParams.append("&pbk=").append(generatePublicKey(privateKey));
                        }

                        JSONArray shortIds = realitySettings.getJSONArray("shortIds");
                        if (shortIds != null && !shortIds.isEmpty()) {
                            String sid = shortIds.getString(0);
                            if (StringUtils.isNotEmpty(sid)) {
                                queryParams.append("&sid=").append(sid);
                            }
                        }
                    }
                }

                // 网络类型
                if (StringUtils.isNotEmpty(network)) {
                    queryParams.append("&type=").append(network);
                }

                if (StringUtils.isEmpty(keyword)) {
                    keyword = "";
                }

                queryParams.append("&headerType=none");

                // 构建完整vless链接
                String vlessLink = "vless://" + userInfo + "@" + hostInfo + "?" + queryParams.toString() + "#" + vpnLine.getName()
                        + "@split@" + keyword + "@split@" + vpnLine.getRegion();
                vlessLinkList.add(vlessLink);

            } catch (Exception e) {

            }
        }

        return vlessLinkList;
    }

    public static String generatePublicKey(String privateKeyBase64) {
//        byte[] privateKey = Base64.decode(privateKeyBase64);
//        if (privateKey.length != X25519.SCALAR_SIZE) {
//            throw new IllegalArgumentException("私钥必须是 32 字节");
//        }
//        byte[] publicKey = new byte[X25519.POINT_SIZE];
//        X25519.scalarMultBase(privateKey, 0, publicKey, 0);
//
//        return new String(Base64.encode(publicKey), StandardCharsets.US_ASCII);
        return privateKeyBase64;
    }

    @Override
    public Map<String, Long> countLinesByStatus() {
        List<Map<String, Object>> stats = vpnLinesMapper.countLinesByStatus();
        Map<String, Long> result = new HashMap<>();
        for (Map<String, Object> stat : stats) {
            result.put(stat.get("status").toString(), Long.valueOf(stat.get("count").toString()));
        }
        return result;
    }

    public void pingStats(){
        List<VpnLines> vpnLines = vpnLinesMapper.selectVpnLinesList(new VpnLines());
        for (VpnLines vpnLine : vpnLines) {
            executorService.execute(() -> {
                int pingDelay = getIcmpPingDelay(vpnLine.getIp(), 3000);

                VpnLines update = new VpnLines();
                update.setId(vpnLine.getId());
                update.setPingDelay(pingDelay);
                vpnLinesMapper.updateVpnLines(update);
            });
        }
    }

    private int getIcmpPingDelay(String ip, int timeout) {
        try {
            String pingCommand = System.getProperty("os.name").toLowerCase().contains("win") ?
                    "ping -n 1 -w " + timeout + " " + ip :
                    "ping -c 1 -W " + (timeout/1000) + " " + ip;

            Process process = Runtime.getRuntime().exec(pingCommand);
            long startTime = System.currentTimeMillis();
            int returnCode = process.waitFor(timeout, TimeUnit.MILLISECONDS) ?
                    process.exitValue() : -1;
            long endTime = System.currentTimeMillis();

            return (int)((returnCode == 0) ? (endTime - startTime) : -1);
        } catch (Exception e) {
            return -1;
        }
    }


}
