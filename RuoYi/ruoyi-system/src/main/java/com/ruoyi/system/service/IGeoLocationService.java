package com.ruoyi.system.service;

import com.ruoyi.system.domain.vo.GeoLocationVO;

/**
 * 出口 IP 归属查询。
 *
 * <p>商用服务商的密钥只保存在服务端配置中，不随任何接口下发给客户端 ——
 * 客户端只负责把经代理探测到的出口 IP 上报上来，归属解析全部在这里完成。
 * 这样密钥不会因客户端被反编译而泄露，配额也能集中管控与缓存复用。
 *
 * @author ruoyi
 */
public interface IGeoLocationService {

    /**
     * 查询 IP 的地理归属。
     *
     * @param ip 出口 IP
     * @return 归属信息；功能未启用、IP 非法或查询失败时返回 null（调用方应保留原值）
     */
    GeoLocationVO resolve(String ip);

    /**
     * 归属查询是否已启用。未采购商用配置时为 false，此时 {@link #resolve} 恒返回 null。
     */
    boolean isEnabled();
}
