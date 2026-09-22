package com.ruoyi.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.system.domain.vo.GeoLocationVO;
import com.ruoyi.system.service.IGeoLocationService;
import com.ruoyi.system.service.ISysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 出口 IP 归属查询实现。
 *
 * <p>配置全部走 sys_config，可在后台直接改，无需改代码或重启：
 * <ul>
 *   <li>{@code chain.geo.enabled}  总开关，未采购商用配置前为 false</li>
 *   <li>{@code chain.geo.provider} ip-api 或 ip2location</li>
 *   <li>{@code chain.geo.endpoint} 留空则用服务商默认地址</li>
 *   <li>{@code chain.geo.apiKey}   商用密钥，仅服务端持有</li>
 * </ul>
 *
 * @author ruoyi
 */
@Service
public class GeoLocationServiceImpl implements IGeoLocationService {

    private static final Logger log = LoggerFactory.getLogger(GeoLocationServiceImpl.class);

    private static final String KEY_ENABLED = "chain.geo.enabled";
    private static final String KEY_PROVIDER = "chain.geo.provider";
    private static final String KEY_ENDPOINT = "chain.geo.endpoint";
    private static final String KEY_API_KEY = "chain.geo.apiKey";

    private static final String DEFAULT_IP_API = "http://ip-api.com/json/";
    private static final String DEFAULT_IP2LOCATION = "https://api.ip2location.io/";

    /** 同一出口 IP 在多个端点上重复出现很常见，缓存可显著省配额。 */
    private final Map<String, GeoLocationVO> cache = new ConcurrentHashMap<>();

    @Autowired
    private ISysConfigService configService;

    @Override
    public boolean isEnabled() {
        return "true".equalsIgnoreCase(StringUtils.trim(configService.selectConfigByKey(KEY_ENABLED)));
    }

    @Override
    public GeoLocationVO resolve(String ip) {
        if (!isEnabled() || StringUtils.isBlank(ip)) {
            return null;
        }
        GeoLocationVO cached = cache.get(ip);
        if (cached != null) {
            return cached;
        }

        try {
            String provider = blankTo(configService.selectConfigByKey(KEY_PROVIDER), "ip-api");
            String apiKey = StringUtils.trim(configService.selectConfigByKey(KEY_API_KEY));
            String endpoint = StringUtils.trim(configService.selectConfigByKey(KEY_ENDPOINT));

            GeoLocationVO result = "ip2location".equalsIgnoreCase(provider)
                    ? queryIp2Location(ip, endpoint, apiKey)
                    : queryIpApi(ip, endpoint, apiKey);

            if (result != null) {
                cache.put(ip, result);
            }
            return result;
        } catch (Exception e) {
            // 归属查询失败不应影响查活主流程，吞掉并保留原有归属值。
            log.warn("出口IP归属查询失败, ip={}, 原因={}", ip, e.getMessage());
            return null;
        }
    }

    private static String blankTo(String value, String fallback) {
        String trimmed = StringUtils.trim(value);
        return StringUtils.isBlank(trimmed) ? fallback : trimmed;
    }

    /**
     * ip-api：免费版仅 HTTP 且限非商业用途，商用需走 pro 域名并带 key。
     */
    private GeoLocationVO queryIpApi(String ip, String endpoint, String apiKey) {
        String base = blankTo(endpoint, DEFAULT_IP_API);
        StringBuilder url = new StringBuilder(base);
        if (!base.endsWith("/")) {
            url.append('/');
        }
        url.append(ip).append("?fields=status,countryCode,regionName,city");
        if (StringUtils.isNotBlank(apiKey)) {
            url.append("&key=").append(apiKey);
        }

        JSONObject json = JSONObject.parseObject(HttpUtils.sendGet(url.toString(), ""));
        if (json == null || !"success".equals(json.getString("status"))) {
            return null;
        }
        return new GeoLocationVO(
                json.getString("countryCode"),
                json.getString("regionName"),
                json.getString("city"));
    }

    /**
     * ip2location.io：必须带 key，免费额度约 3 万次/月。
     */
    private GeoLocationVO queryIp2Location(String ip, String endpoint, String apiKey) {
        if (StringUtils.isBlank(apiKey)) {
            log.warn("ip2location 未配置 apiKey，跳过归属查询");
            return null;
        }
        String base = blankTo(endpoint, DEFAULT_IP2LOCATION);
        String url = base + "?key=" + apiKey + "&ip=" + ip;

        JSONObject json = JSONObject.parseObject(HttpUtils.sendGet(url, ""));
        if (json == null || StringUtils.isBlank(json.getString("country_code"))) {
            return null;
        }
        return new GeoLocationVO(
                json.getString("country_code"),
                json.getString("region_name"),
                json.getString("city_name"));
    }
}
