package com.ruoyi.system.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

@Service
public class Ip2regionService {

    private DatabaseReader reader;

    @PostConstruct
    public void init() throws Exception {
        // 假设你把 GeoLite2-Country.mmdb 放在 resources 目录
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("GeoLite2-Country.mmdb")) {
            if (inputStream == null) {
                throw new IllegalStateException("未找到 GeoLite2-Country.mmdb 文件，请确保它在 resources 目录下。");
            }
            // 将 InputStream 临时写到一个临时文件，因为 DatabaseReader 只能接收 File
            File tempFile = File.createTempFile("GeoLite2-Country", ".mmdb");
            tempFile.deleteOnExit();
            try (OutputStream out = java.nio.file.Files.newOutputStream(tempFile.toPath())) {
                inputStream.transferTo(out);
            }
            reader = new DatabaseReader.Builder(tempFile).build();
        }
    }

    /**
     * 查询 IP 所属国家 ISO 代码，例如 CN、US
     */
    public String getCountryCode(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = reader.country(ipAddress);
            return response.getCountry().getIsoCode();
        } catch (Exception e) {
            // 查询失败时返回默认值
            return "CN";
        }
    }

    /**
     * 查询 IP 所属国家名称，例如 China、United States
     */
    public String getCountryName(String ip) {
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = reader.country(ipAddress);
            return response.getCountry().getName();
        } catch (Exception e) {
            return "China";
        }
    }
}
