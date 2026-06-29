package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;

public class VpnLinesExcel extends VpnLines{
    @Excel(name = "Config配置")
    private String configJson;


    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }
}
