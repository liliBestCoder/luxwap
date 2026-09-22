package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.XrayChainProxyConfig;
import com.ruoyi.system.domain.XrayChainProxyNode;
import com.ruoyi.system.domain.dto.ChainProxyActiveConfigDTO;
import com.ruoyi.system.domain.dto.ChainProxyImportDTO;
import com.ruoyi.system.domain.dto.ChainProxyStatusReportDTO;
import com.ruoyi.system.domain.vo.ChainProxyNodeVO;
import com.ruoyi.system.domain.vo.GeoLocationVO;
import com.ruoyi.system.mapper.XrayChainProxyConfigMapper;
import com.ruoyi.system.mapper.XrayChainProxyNodeMapper;
import com.ruoyi.system.service.IGeoLocationService;
import com.ruoyi.system.service.IXrayChainProxyService;
import com.ruoyi.system.util.ProxyCipherUtils;
import com.ruoyi.system.util.ProxyParserUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 链式代理业务实现类
 *
 * @author ruoyi
 */
@Service
public class XrayChainProxyServiceImpl implements IXrayChainProxyService {

    private static final Logger log = LoggerFactory.getLogger(XrayChainProxyServiceImpl.class);

    @Autowired
    private XrayChainProxyNodeMapper nodeMapper;

    @Autowired
    private XrayChainProxyConfigMapper configMapper;

    @Autowired
    private IGeoLocationService geoLocationService;

    @Override
    public List<ChainProxyNodeVO> queryUserProxyList(Long userId, String protocol, Integer aliveStatus, String countryCode, String remark) {
        XrayChainProxyNode condition = new XrayChainProxyNode();
        condition.setUserId(userId);
        condition.setProtocol(protocol);
        condition.setAliveStatus(aliveStatus);
        condition.setCountryCode(countryCode);
        condition.setRemark(remark);

        List<XrayChainProxyNode> list = nodeMapper.selectXrayChainProxyNodeList(condition);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(ChainProxyNodeVO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchImportProxies(Long userId, ChainProxyImportDTO dto) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (dto == null || StringUtils.isBlank(dto.getProxyText())) {
            result.put("total", 0);
            result.put("imported", 0);
            result.put("duplicates", 0);
            result.put("failed", 0);
            result.put("message", "导入内容为空");
            return result;
        }

        String[] lines = dto.getProxyText().split("\\r?\\n");
        int total = 0;
        int imported = 0;
        int duplicates = 0;
        int failed = 0;
        List<String> failedLines = new ArrayList<>();

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            total++;
            // 一次最多导入 30 条
            if (total > 30) {
                failedLines.add("超出单次添加 30 条上限，已忽略后续内容: " + line);
                failed++;
                continue;
            }

            try {
                XrayChainProxyNode node = ProxyParserUtils.parseLine(line, dto.getDefaultProtocol());
                if (node == null) {
                    failed++;
                    failedLines.add("格式解析错误: " + line);
                    continue;
                }

                // 重复校验
                XrayChainProxyNode existing = nodeMapper.checkNodeExists(userId, node.getHost(), node.getPort());
                if (existing != null) {
                    duplicates++;
                    continue;
                }

                // 生成防遍历的对外安全业务唯一Key (UUID/NanoID)
                node.setNodeKey("cp_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));

                // 密码安全加密存储
                String plainPassword = node.getPasswordCipher();
                node.setPasswordCipher(ProxyCipherUtils.encrypt(plainPassword));

                node.setUserId(userId);
                node.setSourceType(1); // 用户自建
                node.setAliveStatus(0); // 未检测
                node.setLatencyMs(0);
                node.setSortOrder(0);
                node.setCreateBy(String.valueOf(userId));

                nodeMapper.insertXrayChainProxyNode(node);
                imported++;
            } catch (IllegalArgumentException e) {
                failed++;
                failedLines.add(e.getMessage() + ": " + line);
            } catch (Exception e) {
                log.error("导入代理行失败: {}, 原因: {}", line, e.getMessage());
                failed++;
                failedLines.add("系统内部异常: " + line);
            }
        }

        result.put("total", total);
        result.put("imported", imported);
        result.put("duplicates", duplicates);
        result.put("failed", failed);
        result.put("failedDetails", failedLines);
        result.put("message", String.format("共 %d 条，有效导入 %d 条，重复 %d 条，失败 %d 条", total, imported, duplicates, failed));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNodeStatusFromClient(Long userId, List<ChainProxyStatusReportDTO> reports) {
        if (reports == null || reports.isEmpty()) {
            return;
        }

        for (ChainProxyStatusReportDTO r : reports) {
            if (StringUtils.isBlank(r.getNodeKey())) continue;

            XrayChainProxyNode node = nodeMapper.selectXrayChainProxyNodeByKey(r.getNodeKey());
            if (node == null || !node.getUserId().equals(userId)) {
                continue; // 严格租户隔离，禁止篡改其他用户端点
            }

            if (r.getAliveStatus() != null) node.setAliveStatus(r.getAliveStatus());
            if (r.getLatencyMs() != null) node.setLatencyMs(r.getLatencyMs());
            if (StringUtils.isNotBlank(r.getCountryCode())) node.setCountryCode(r.getCountryCode());

            // 出口 IP 变了才重新查归属：出口通常长期稳定，这一层判断比内存缓存更省配额。
            if (StringUtils.isNotBlank(r.getExitIp())) {
                boolean exitChanged = !r.getExitIp().equals(node.getExitIp());
                node.setExitIp(r.getExitIp());
                if (exitChanged) {
                    GeoLocationVO geo = geoLocationService.resolve(r.getExitIp());
                    if (geo != null) {
                        if (StringUtils.isNotBlank(geo.getCountryCode())) {
                            node.setCountryCode(geo.getCountryCode());
                        }
                        node.setRegion(geo.getRegion());
                        node.setCity(geo.getCity());
                    }
                }
            }
            node.setLastCheckTime(new Date());

            nodeMapper.updateXrayChainProxyNode(node);
        }
    }

    @Override
    public XrayChainProxyConfig getUserChainConfig(Long userId) {
        XrayChainProxyConfig config = configMapper.selectConfigByUserId(userId);
        if (config == null) {
            config = new XrayChainProxyConfig();
            config.setUserId(userId);
            config.setIsEnabled(0);
            config.setMode("fixed_exit");
        }
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUserChainConfig(Long userId, ChainProxyActiveConfigDTO dto) {
        XrayChainProxyConfig current = configMapper.selectConfigByUserId(userId);
        Integer enabled = dto.resolveEnabled();
        String mode = dto.resolveMode();
        if (current == null) {
            XrayChainProxyConfig newConfig = new XrayChainProxyConfig();
            newConfig.setUserId(userId);
            newConfig.setIsEnabled(enabled != null ? enabled : 0);
            newConfig.setActiveNodeId(dto.getActiveNodeId());
            newConfig.setActiveNodeKey(dto.getActiveNodeKey());
            newConfig.setExitLineId(dto.getExitLineId());
            newConfig.setMode(mode != null ? mode : "fixed_exit");
            configMapper.insertConfig(newConfig);
        } else {
            if (enabled != null) current.setIsEnabled(enabled);
            if (dto.getActiveNodeId() != null) current.setActiveNodeId(dto.getActiveNodeId());
            if (dto.getActiveNodeKey() != null) current.setActiveNodeKey(dto.getActiveNodeKey());
            if (dto.getExitLineId() != null) current.setExitLineId(dto.getExitLineId());
            if (mode != null) current.setMode(mode);
            configMapper.updateConfig(current);
        }
    }

    @Override
    public boolean deleteProxyByKey(Long userId, String nodeKey) {
        if (StringUtils.isBlank(nodeKey)) return false;
        return nodeMapper.deleteXrayChainProxyNodeByKey(nodeKey, userId) > 0;
    }

    @Override
    public boolean batchDeleteProxiesByKeys(Long userId, String[] nodeKeys) {
        if (nodeKeys == null || nodeKeys.length == 0) return false;
        return nodeMapper.deleteXrayChainProxyNodeByKeys(nodeKeys, userId) > 0;
    }

    @Override
    public boolean deleteProxy(Long userId, Long nodeId) {
        return nodeMapper.deleteXrayChainProxyNodeById(nodeId, userId) > 0;
    }

    @Override
    public boolean batchDeleteProxies(Long userId, Long[] nodeIds) {
        if (nodeIds == null || nodeIds.length == 0) return false;
        return nodeMapper.deleteXrayChainProxyNodeByIds(nodeIds, userId) > 0;
    }
}
