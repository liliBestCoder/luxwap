package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.XrayPointRecord;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.domain.XrayUserPoint;
import com.ruoyi.system.mapper.XrayUserMapper;
import com.ruoyi.system.mapper.XrayUserPointMapper;
import com.ruoyi.system.service.IXrayUserPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class XrayUserPointServiceImpl implements IXrayUserPointService {
    private static final Logger log = LoggerFactory.getLogger(XrayUserPointServiceImpl.class);

    private static final long BYTES_PER_GB = 1073741824L;
    private static final int SETTLE_BATCH_SIZE = 500;

    /** 每消耗 1GB 流量发放的积分。 */
    @Value("${luxwap.points.perGb:10}")
    private long pointsPerGb;

    /** 兑换 1GB 流量需要消耗的积分。 */
    @Value("${luxwap.points.exchangePerGb:100}")
    private long pointsPerGbExchange;

    @Autowired
    private XrayUserPointMapper pointMapper;

    @Autowired
    private XrayUserMapper userMapper;

    @Override
    public long getPointsPerGb() {
        return pointsPerGb;
    }

    @Override
    public long getPointsPerGbExchange() {
        return pointsPerGbExchange;
    }

    @Override
    public XrayUserPoint getOrCreateAccount(Long userId) {
        XrayUserPoint account = pointMapper.selectByUserId(userId);
        if (account != null) {
            return account;
        }
        XrayUser user = userMapper.selectXrayUserById(userId);
        XrayUserPoint created = new XrayUserPoint();
        created.setUserId(userId);
        created.setBalance(0L);
        created.setTotalEarned(0L);
        // 新账户水位对齐当前已用流量，只对开户之后新产生的用量发积分。
        created.setSettledTraffic(user == null ? 0L : Optional.ofNullable(user.getUsedTraffic()).orElse(0L));
        pointMapper.insertAccount(created);
        return pointMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public XrayUserPoint settleTrafficPoints(Long userId) {
        XrayUserPoint account = getOrCreateAccount(userId);
        XrayUser user = userMapper.selectXrayUserById(userId);
        if (user == null) {
            return account;
        }

        long used = Optional.ofNullable(user.getUsedTraffic()).orElse(0L);
        long settled = Optional.ofNullable(account.getSettledTraffic()).orElse(0L);
        long pending = used - settled;
        if (pending < BYTES_PER_GB || pointsPerGb <= 0) {
            // 不足一档就留到下次，零头不会被吞掉。
            return account;
        }

        long wholeGb = pending / BYTES_PER_GB;
        long earned = wholeGb * pointsPerGb;
        long newSettled = settled + wholeGb * BYTES_PER_GB;

        int updated = pointMapper.settle(userId, earned, newSettled, settled);
        if (updated == 0) {
            // 水位被另一个结算改掉了，这轮放弃，下次再算。
            return pointMapper.selectByUserId(userId);
        }

        XrayUserPoint latest = pointMapper.selectByUserId(userId);
        XrayPointRecord record = new XrayPointRecord();
        record.setUserId(userId);
        record.setChangeAmount(earned);
        record.setBalanceAfter(latest.getBalance());
        record.setType(XrayPointRecord.TYPE_EARN_TRAFFIC);
        record.setRemark("使用流量 " + wholeGb + "GB 发放积分");
        pointMapper.insertRecord(record);
        return latest;
    }

    @Override
    public List<XrayPointRecord> listRecords(Long userId, int limit) {
        return pointMapper.selectRecordsByUserId(userId, limit <= 0 ? 20 : Math.min(limit, 200));
    }

    @Override
    @Transactional
    public long exchangeForTraffic(Long userId, long points) {
        if (points <= 0) {
            throw new IllegalArgumentException("兑换积分必须大于 0");
        }
        if (pointsPerGbExchange <= 0) {
            throw new IllegalStateException("积分兑换比例未配置");
        }
        if (points % pointsPerGbExchange != 0) {
            throw new IllegalArgumentException("兑换积分必须是 " + pointsPerGbExchange + " 的整数倍");
        }

        getOrCreateAccount(userId);
        int deducted = pointMapper.deduct(userId, points);
        if (deducted == 0) {
            throw new IllegalArgumentException("积分余额不足");
        }

        long grantedGb = points / pointsPerGbExchange;
        long grantedBytes = grantedGb * BYTES_PER_GB;

        XrayUser user = userMapper.selectXrayUserById(userId);
        XrayUser update = new XrayUser();
        update.setId(userId);
        update.setTotalTraffic(Optional.ofNullable(user.getTotalTraffic()).orElse(0L) + grantedBytes);
        userMapper.updateXrayUser(update);

        XrayUserPoint latest = pointMapper.selectByUserId(userId);
        XrayPointRecord record = new XrayPointRecord();
        record.setUserId(userId);
        record.setChangeAmount(-points);
        record.setBalanceAfter(latest.getBalance());
        record.setType(XrayPointRecord.TYPE_EXCHANGE_TRAFFIC);
        record.setRemark("兑换流量 " + grantedGb + "GB");
        pointMapper.insertRecord(record);
        return grantedBytes;
    }

    /** 兜底结算：客户端不查积分的用户也能按用量拿到积分。 */
    @Scheduled(fixedDelay = 300000L, initialDelay = 60000L)
    public void settlePendingUsers() {
        try {
            List<Long> userIds = pointMapper.selectUserIdsPendingSettle(SETTLE_BATCH_SIZE);
            for (Long userId : userIds) {
                try {
                    settleTrafficPoints(userId);
                } catch (Exception e) {
                    log.warn("settle points failed for user {}", userId, e);
                }
            }
        } catch (Exception e) {
            log.error("scheduled point settlement failed", e);
        }
    }
}
