package com.ruoyi.system.service;

import com.ruoyi.system.domain.XrayPointRecord;
import com.ruoyi.system.domain.XrayUserPoint;

import java.util.List;

public interface IXrayUserPointService {
    /** 取账户，没有则建。 */
    XrayUserPoint getOrCreateAccount(Long userId);

    /** 按该用户最新的已用流量结算一次积分，返回结算后的账户。 */
    XrayUserPoint settleTrafficPoints(Long userId);

    List<XrayPointRecord> listRecords(Long userId, int limit);

    /**
     * 消耗积分兑换流量，成功返回兑换到的流量字节数。
     * 积分不足或参数非法抛 IllegalArgumentException。
     */
    long exchangeForTraffic(Long userId, long points);

    /** 每 GB 流量发放的积分。 */
    long getPointsPerGb();

    /** 兑换 1GB 流量需要的积分。 */
    long getPointsPerGbExchange();
}
