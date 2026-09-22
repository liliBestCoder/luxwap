package com.ruoyi.system.domain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.io.Serializable;

/**
 * 链式代理批量导入请求参数
 *
 * @author ruoyi
 */
public class ChainProxyImportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 批量代理文本（每行一个）。客户端历史上以 proxyLines 发送，两个名字都接受。 */
    @JsonAlias("proxyLines")
    private String proxyText;

    /** 默认代理类型（当未指定协议时采用，默认 socks5） */
    private String defaultProtocol = "socks5";

    public String getProxyText() {
        return proxyText;
    }

    public void setProxyText(String proxyText) {
        this.proxyText = proxyText;
    }

    public String getDefaultProtocol() {
        return defaultProtocol;
    }

    public void setDefaultProtocol(String defaultProtocol) {
        this.defaultProtocol = defaultProtocol;
    }
}
