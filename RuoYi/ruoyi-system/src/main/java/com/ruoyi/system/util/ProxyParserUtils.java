package com.ruoyi.system.util;

import com.ruoyi.system.domain.XrayChainProxyNode;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 链式代理字符串智能解析与安全校验工具
 *
 * @author ruoyi
 */
public class ProxyParserUtils {

    // 匹配备注: {备注内容} 或 (备注内容)
    private static final Pattern REMARK_PATTERN = Pattern.compile("[{(（](.*?)[})）]");
    // 匹配刷新URL: [刷新URL]
    private static final Pattern REFRESH_URL_PATTERN = Pattern.compile("\\[(https?://[^\\]]+)\\]");
    // 匹配协议头: socks5://, http://, https://
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^(socks5|http|https)://", Pattern.CASE_INSENSITIVE);

    /**
     * 单行代理字符串解析
     *
     * @param rawLine 原始行文本
     * @param defaultProtocol 默认协议（未指定时，如 socks5）
     * @return 解析后的实体对象（未加密 password，保留在 passwordCipher 字段供后续加密）
     */
    public static XrayChainProxyNode parseLine(String rawLine, String defaultProtocol) {
        if (StringUtils.isBlank(rawLine)) {
            return null;
        }

        String line = rawLine.trim();
        XrayChainProxyNode node = new XrayChainProxyNode();

        // 1. 提取刷新 URL（如有，且区分 IPv6 的方括号）
        Matcher refreshMatcher = REFRESH_URL_PATTERN.matcher(line);
        if (refreshMatcher.find()) {
            node.setRefreshUrl(refreshMatcher.group(1).trim());
            line = line.replace(refreshMatcher.group(0), "").trim();
        }

        // 2. 提取备注信息
        Matcher remarkMatcher = REMARK_PATTERN.matcher(line);
        if (remarkMatcher.find()) {
            node.setRemark(remarkMatcher.group(1).trim());
            line = line.replace(remarkMatcher.group(0), "").trim();
        }

        // 3. 提取协议 Scheme
        String protocol = StringUtils.isNotBlank(defaultProtocol) ? defaultProtocol.toLowerCase() : "socks5";
        Matcher schemeMatcher = SCHEME_PATTERN.matcher(line);
        if (schemeMatcher.find()) {
            protocol = schemeMatcher.group(1).toLowerCase();
            line = line.substring(schemeMatcher.end()).trim();
        }
        node.setProtocol(protocol);

        // 4. 解析用户认证信息与 Host:Port
        String username = "";
        String password = "";
        String host = "";
        int port = 0;

        // 格式 A: user:pass@host:port
        if (line.contains("@")) {
            int atIndex = line.lastIndexOf("@");
            String userInfo = line.substring(0, atIndex);
            String hostPortPart = line.substring(atIndex + 1);

            if (userInfo.contains(":")) {
                int colonIndex = userInfo.indexOf(":");
                username = userInfo.substring(0, colonIndex);
                password = userInfo.substring(colonIndex + 1);
            } else {
                username = userInfo;
            }

            HostAndPort hp = parseHostAndPort(hostPortPart);
            if (hp == null) return null;
            host = hp.host;
            port = hp.port;
        } else {
            // 格式 B: host:port:user:pass 或 host:port
            // 考虑 IPv6 地址可能为 [2001:db8::1]:8000:user:pass
            if (line.startsWith("[")) {
                int closingBracket = line.indexOf("]");
                if (closingBracket > 0 && line.length() > closingBracket + 1 && line.charAt(closingBracket + 1) == ':') {
                    host = line.substring(1, closingBracket);
                    String rest = line.substring(closingBracket + 2);
                    String[] parts = rest.split(":", -1);
                    try {
                        port = Integer.parseInt(parts[0].trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    if (parts.length >= 3) {
                        username = parts[1].trim();
                        password = parts[2].trim();
                    }
                } else {
                    return null;
                }
            } else {
                String[] parts = line.split(":", -1);
                if (parts.length == 2) {
                    // host:port
                    host = parts[0].trim();
                    try {
                        port = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else if (parts.length >= 4) {
                    // host:port:user:pass
                    host = parts[0].trim();
                    try {
                        port = Integer.parseInt(parts[1].trim());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                    username = parts[2].trim();
                    password = parts[3].trim();
                } else {
                    return null;
                }
            }
        }

        if (StringUtils.isBlank(host) || port <= 0 || port > 65535) {
            return null;
        }

        // 5. SSRF 与回环拦截校验
        if (isBlockedAddress(host)) {
            throw new IllegalArgumentException("禁止添加内网或本地回环代理地址: " + host);
        }

        node.setHost(host);
        node.setPort(port);
        node.setUsername(username);
        node.setPasswordCipher(password); // 临时存放明文，待加密后落库

        return node;
    }

    private static class HostAndPort {
        String host;
        int port;
        HostAndPort(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    private static HostAndPort parseHostAndPort(String hostPortPart) {
        if (hostPortPart.startsWith("[")) {
            int close = hostPortPart.indexOf("]");
            if (close > 0 && hostPortPart.length() > close + 1 && hostPortPart.charAt(close + 1) == ':') {
                String host = hostPortPart.substring(1, close);
                try {
                    int port = Integer.parseInt(hostPortPart.substring(close + 2).trim());
                    return new HostAndPort(host, port);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        } else {
            int colon = hostPortPart.lastIndexOf(":");
            if (colon <= 0) return null;
            String host = hostPortPart.substring(0, colon).trim();
            try {
                int port = Integer.parseInt(hostPortPart.substring(colon + 1).trim());
                return new HostAndPort(host, port);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    /**
     * 校验是否属于内网或回环地址 (防 SSRF)
     */
    public static boolean isBlockedAddress(String host) {
        if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "::1".equals(host)) {
            return true;
        }
        try {
            // 如果是标准 IP 格式，校验私有网段
            if (host.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
                String[] octets = host.split("\\.");
                int first = Integer.parseInt(octets[0]);
                int second = Integer.parseInt(octets[1]);
                if (first == 127 || first == 0) return true; // 回环与保留
                if (first == 10) return true; // 10.0.0.0/8
                if (first == 172 && (second >= 16 && second <= 31)) return true; // 172.16.0.0/12
                if (first == 192 && second == 168) return true; // 192.168.0.0/16
                if (first == 169 && second == 254) return true; // 链路本地 169.254.0.0/16
            }
        } catch (Exception ignored) {}
        return false;
    }
}
