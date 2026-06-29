package com.ruoyi.system.util;
import com.alibaba.fastjson.JSON;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;
import com.ruoyi.system.domain.VpnLinesExcel;
import org.apache.commons.lang3.StringUtils;

public class CsvUtils {

    private static final Map<String, String> REGION_MAP;
    private static final Map<String, String> TYPE_MAP;

    static {
        Map<String, String> region = new HashMap<>();
        region.put("亚洲", "asia");
        region.put("欧洲", "europe");
        region.put("北美洲", "north_america");
        region.put("南美洲", "south_america");
        REGION_MAP = Collections.unmodifiableMap(region);

        Map<String, String> type = new HashMap<>();
        type.put("光纤", "fiber");
        type.put("电缆", "cable");
        type.put("无线", "wireless");
        type.put("卫星", "satellite");
        TYPE_MAP = Collections.unmodifiableMap(type);
    }

    // IPv4 正则
    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.|$)){4}$");

    /**
     * 从 CSV 读取 VPN 行配置
     * @param inputStream 输入流
     * @return VpnLinesExcel 列表
     */
    public static List<VpnLinesExcel> readCsvToVpnLine(InputStream inputStream) throws IOException {
        List<VpnLinesExcel> vpnLineList = new ArrayList<>();
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(new InputStreamReader(inputStream));

        for (CSVRecord record : records) {
            if (record.size() < 11) {
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行列数不足，期望至少 11 列，实际: " + record.size());
            }

            VpnLinesExcel vpnLine = new VpnLinesExcel();

            // region (第0列) 中文 -> 英文
            String regionCn = record.get(0);
            String regionEn = REGION_MAP.get(regionCn);
            if (regionEn == null) {
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: 无效的 region 值: " + regionCn);
            }
            vpnLine.setRegion(regionEn);

            // name (第1列)
            vpnLine.setName(record.get(1));

            // type (第2列) 中文 -> 英文
            String typeCn = record.get(2);
            String typeEn = TYPE_MAP.get(typeCn);
            if (typeEn == null) {
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: 无效的 type 值: " + typeCn);
            }
            vpnLine.setType(typeEn);

            // ip 校验 (第3列)
            String ip = record.get(3);
            if (!IPV4_PATTERN.matcher(ip).matches()) {
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: 无效的 IP: " + ip);
            }
            vpnLine.setIp(ip);

            // 数值字段
            vpnLine.setBandwidth(parseInt(record.get(4), "bandwidth", record));
            vpnLine.setTotalTraffic(parseLong(record.get(5), "total_traffic", record));
            vpnLine.setMaxConnectionCnt(parseInt(record.get(6), "max_connection_cnt", record));
            vpnLine.setPingOffset(parseInt(record.get(7), "ping_offset", record));

            int port = parseInt(record.get(8), "port", record);
            if (port <= 0 || port >= 65535) {
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: 端口号必须在 1~65535 范围内: " + port);
            }
            vpnLine.setPort(port);

            String keyword = record.get(9);
            if (StringUtils.isBlank(keyword)) {
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: 筛选词不能为空");
            }
            // keyword (第9列)
            vpnLine.setKeyword(keyword);

            String configJson = record.get(10);
            if (StringUtils.isBlank(configJson)) {
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: 配置不能为空");
            }
            try{
                JSON.parseObject(configJson);
            }catch (Exception e){
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: 配置格式为空");
            }
            // config_json (第10列)
            vpnLine.setConfigJson(configJson);
            vpnLineList.add(vpnLine);
        }

        return vpnLineList;
    }

    private static int parseInt(String val, String field, CSVRecord record) {
        if (StringUtils.isBlank(val)) {
            throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: " + field + " 不能为空");
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: " + field + " 必须是整数，实际值: " + val);
        }
    }

    private static long parseLong(String val, String field, CSVRecord record) {
        if (StringUtils.isBlank(val)) {
            throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: " + field + " 不能为空");
        }
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: " + field + " 必须是整数，实际值: " + val);
        }
    }
}
