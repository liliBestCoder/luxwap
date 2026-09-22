package com.ruoyi.system.domain.vo;

import java.io.Serializable;

/**
 * IP 地理归属查询结果
 *
 * @author ruoyi
 */
public class GeoLocationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 国家代码，如 US、JP */
    private String countryCode;

    /** 省份 / 州 */
    private String region;

    /** 城市 */
    private String city;

    public GeoLocationVO() {
    }

    public GeoLocationVO(String countryCode, String region, String city) {
        this.countryCode = countryCode;
        this.region = region;
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
