package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.XrayPointRecord;
import com.ruoyi.system.domain.XrayUserPoint;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XrayUserPointMapper {
    XrayUserPoint selectByUserId(@Param("userId") Long userId);

    int insertAccount(XrayUserPoint account);

    /**
     * 按水位做乐观更新：只有当库里的水位仍是结算时读到的值才写入，
     * 避免定时任务与用户请求并发结算导致积分重复发放。
     */
    int settle(@Param("userId") Long userId,
               @Param("earned") Long earned,
               @Param("newSettledTraffic") Long newSettledTraffic,
               @Param("expectSettledTraffic") Long expectSettledTraffic);

    int deduct(@Param("userId") Long userId, @Param("amount") Long amount);

    int insertRecord(XrayPointRecord record);

    List<XrayPointRecord> selectRecordsByUserId(@Param("userId") Long userId,
                                                @Param("limit") Integer limit);

    /** 已用流量超过结算水位、需要发放积分的用户。 */
    List<Long> selectUserIdsPendingSettle(@Param("limit") Integer limit);
}
