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
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: 配置/VLESS 链接不能为空");
            }
            String vlessUrl = configJson.trim();
            if (!vlessUrl.startsWith("vless://")) {
                throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: 配置字段必须是以 vless:// 开头的链接");
            }
            validateAndTestVlessUrl(vlessUrl, record);
            // config_json (第10列) 存储原生的 vless:// 链接
            vpnLine.setConfigJson(vlessUrl);
            vpnLineList.add(vpnLine);
        }

        return vpnLineList;
    }

    /**
     * 校验并测试 VLESS 链接的合法性与网络/协议握手可用性
     */
    private static void validateAndTestVlessUrl(String url, CSVRecord record) {
        String raw = url.substring(8);
        int hashIdx = raw.indexOf('#');
        String mainPart = (hashIdx != -1) ? raw.substring(0, hashIdx) : raw;

        int queryIdx = mainPart.indexOf('?');
        String authority = (queryIdx != -1) ? mainPart.substring(0, queryIdx) : mainPart;
        String queryString = (queryIdx != -1) ? mainPart.substring(queryIdx + 1) : "";

        int atIdx = authority.lastIndexOf('@');
        if (atIdx == -1) {
            throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: VLESS 链接格式无效，缺失 '@' 符号");
        }
        String uuidStr = authority.substring(0, atIdx);
        String hostPort = authority.substring(atIdx + 1);

        int colonIdx = hostPort.lastIndexOf(':');
        if (colonIdx == -1) {
            throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: VLESS 链接格式无效，缺失端口号冒号");
        }
        String host = hostPort.substring(0, colonIdx);
        int port;
        try {
            port = Integer.parseInt(hostPort.substring(colonIdx + 1));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: VLESS 链接端口号无效");
        }

        UUID uuid;
        try {
            uuid = UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: VLESS 链接 UUID 格式无效: " + uuidStr);
        }

        Map<String, String> queryParams = new HashMap<>();
        if (StringUtils.isNotBlank(queryString)) {
            for (String pair : queryString.split("&")) {
                int eq = pair.indexOf('=');
                if (eq != -1) {
                    queryParams.put(pair.substring(0, eq), pair.substring(eq + 1));
                } else {
                    queryParams.put(pair, "");
                }
            }
        }
        String security = queryParams.getOrDefault("security", "none");
        String sni = queryParams.get("sni");

        // 执行 Socket TCP 连接与 VLESS 协议握手测试
        testVlessHandshake(host, port, uuid, security, sni, record);
    }

    /**
     * 向目标 VLESS 节点发起 Socket TCP 连接与 VLESS/TLS/REALITY 协议 Header 握手测试
     */
    private static void testVlessHandshake(String host, int port, UUID uuid, String security, String sni, CSVRecord record) {
        int timeout = 3500;
        java.net.Socket socket = null;
        try {
            socket = new java.net.Socket();
            socket.connect(new java.net.InetSocketAddress(host, port), timeout);
            socket.setSoTimeout(timeout);

            // 如果是 TLS 或 REALITY 加密，需要包装为 SSLSocket 发起 TLS 握手
            if ("tls".equalsIgnoreCase(security) || "reality".equalsIgnoreCase(security)) {
                javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
                sslContext.init(null, new javax.net.ssl.TrustManager[]{
                    new javax.net.ssl.X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
                }, new java.security.SecureRandom());

                javax.net.ssl.SSLSocketFactory factory = sslContext.getSocketFactory();
                javax.net.ssl.SSLSocket sslSocket = (javax.net.ssl.SSLSocket) factory.createSocket(socket, host, port, true);

                // 设置 SNI 扩展
                String serverName = StringUtils.isNotBlank(sni) ? sni : host;
                javax.net.ssl.SSLParameters sslParams = sslSocket.getSSLParameters();
                sslParams.setServerNames(java.util.Collections.singletonList(new java.net.SNIHostName(serverName)));
                sslSocket.setSSLParameters(sslParams);

                sslSocket.startHandshake();
                socket = sslSocket;
            }

            // 构建 VLESS 协议 Header 报文 (Version 0 + 16字节 UUID + 目标地址)
            byte[] vlessHeader = buildVlessHeaderBytes(uuid, "example.com", 80);
            java.io.OutputStream out = socket.getOutputStream();
            out.write(vlessHeader);
            out.flush();

            // 检查服务器是否在握手后立即关闭/重置连接
            java.io.InputStream in = socket.getInputStream();
            socket.setSoTimeout(1000);
            byte[] buf = new byte[1];
            try {
                int read = in.read(buf);
                if (read == -1) {
                    throw new IOException("服务器在发送 VLESS 握手报文后立即断开了连接 (可能 UUID 无效或节点未启动 VLESS 服务)");
                }
            } catch (java.net.SocketTimeoutException ste) {
                // 读取超时说明连接正常保持未被服务端 RST 重置，VLESS 握手测试通过
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("第 " + record.getRecordNumber() + " 行: VLESS 节点协议握手失败 (" + host + ":" + port + ") - " + e.getMessage());
        } finally {
            if (socket != null) {
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }

    private static byte[] buildVlessHeaderBytes(UUID uuid, String domain, int targetPort) {
        java.nio.ByteBuffer bb = java.nio.ByteBuffer.allocate(50 + domain.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        bb.put((byte) 0x00); // Protocol version
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 7; i >= 0; i--) {
            bb.put((byte) (msb >>> (i * 8)));
        }
        for (int i = 7; i >= 0; i--) {
            bb.put((byte) (lsb >>> (i * 8)));
        }
        bb.put((byte) 0x00); // Addons length 0
        bb.put((byte) 0x01); // Command 1 (TCP)
        bb.putShort((short) targetPort);
        bb.put((byte) 0x02); // Address type 2 (Domain)
        byte[] domainBytes = domain.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        bb.put((byte) domainBytes.length);
        bb.put(domainBytes);

        byte[] result = new byte[bb.position()];
        bb.flip();
        bb.get(result);
        return result;
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
