package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.XrayDistributorsConfig;
import com.ruoyi.system.domain.XrayPacket;
import com.ruoyi.system.domain.XrayPaymentNotifyLog;
import com.ruoyi.system.domain.XrayPaymentOrder;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.mapper.XrayDistributorsConfigMapper;
import com.ruoyi.system.mapper.XrayPacketMapper;
import com.ruoyi.system.mapper.XrayPaymentNotifyLogMapper;
import com.ruoyi.system.mapper.XrayPaymentOrderMapper;
import com.ruoyi.system.mapper.XrayPaymentSubmitTokenMapper;
import com.ruoyi.system.mapper.XrayUserMapper;
import com.ruoyi.system.service.IXrayPaymentOrderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class XrayPaymentOrderServiceImpl implements IXrayPaymentOrderService {

    private static final Logger log = LoggerFactory.getLogger(XrayPaymentOrderServiceImpl.class);

    @Resource
    private XrayPaymentOrderMapper orderMapper;
    @Resource
    private XrayPaymentSubmitTokenMapper submitTokenMapper;
    @Resource
    private XrayPaymentNotifyLogMapper notifyLogMapper;
    @Resource
    private XrayPacketMapper packetMapper;
    @Resource
    private XrayUserMapper userMapper;
    @Resource
    private XrayDistributorsConfigMapper distributorsConfigMapper;

    @Override
    public void createOrder(XrayPaymentOrder order) {
        orderMapper.insertXrayPaymentOrder(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrderWithSubmitToken(XrayPaymentOrder order, String submitToken) {
        int usedRows = submitTokenMapper.useSubmitToken(submitToken, order.getUserId(), order.getOrderNo());
        if (usedRows != 1) {
            throw new IllegalStateException("submit token is invalid, expired, or already used");
        }
        order.setSubmitToken(submitToken);
        orderMapper.insertXrayPaymentOrder(order);
    }

    @Override
    public List<XrayPaymentOrder> getOrders(XrayPaymentOrder order) {
        return orderMapper.selectXrayPaymentOrderList(order);
    }

    @Override
    public XrayPaymentOrder getOrderByOrderNo(String orderNo) {
        return orderMapper.selectXrayPaymentOrderByOrderNo(orderNo);
    }

    @Override
    public XrayPaymentOrder getOrderBySubmitToken(String submitToken) {
        return orderMapper.selectXrayPaymentOrderBySubmitToken(submitToken);
    }

    @Override
    public XrayPaymentOrder getReusablePendingOrder(XrayPaymentOrder order) {
        return orderMapper.selectReusablePendingOrder(order);
    }

    @Override
    public void updateOrderStatus(String orderNo, String status) {
        XrayPaymentOrder order = new XrayPaymentOrder();
        order.setOrderNo(orderNo);
        order.setStatus(status);
        order.setUpdatedAt(new java.util.Date());
        orderMapper.updateXrayPaymentOrderStatus(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markSuccessAndActivate(XrayPaymentOrder order, XrayPaymentNotifyLog log, String tradeNo, BigDecimal paidAmount, String paidCurrency, Date paidAt) {
        notifyLogMapper.insertNotifyLog(log);

        XrayPaymentOrder update = new XrayPaymentOrder();
        update.setOrderNo(order.getOrderNo());
        update.setTradeNo(tradeNo);
        update.setPaidAmount(paidAmount);
        update.setPaidCurrency(paidCurrency);
        update.setPaidAt(paidAt);
        int updatedRows = orderMapper.markOrderSuccessIfNotPaid(update);
        if (updatedRows == 0) {
            notifyLogMapper.markHandled(log.getId());
            return false;
        }

        activatePacketForOrder(order);
        notifyLogMapper.markHandled(log.getId());
        return true;
    }

    @Override
    public void saveNotifyLog(XrayPaymentNotifyLog log) {
        notifyLogMapper.insertNotifyLog(log);
    }

    private void activatePacketForOrder(XrayPaymentOrder order) {
        XrayPacket packet = packetMapper.selectPacketById(order.getPacketId());
        XrayUser user = userMapper.selectXrayUserById(order.getUserId());
        if (packet == null || user == null) {
            throw new IllegalStateException("order packet or user does not exist");
        }

        long grantedTraffic = packet.totalTrafficBytes();
        if (grantedTraffic <= 0L) {
            throw new IllegalStateException("packet traffic is invalid");
        }

        grantedTraffic += firstChargeBonusTraffic(user);

        // 流量是累加配额：老配额没用完的部分继续保留，充值只往上叠加。
        long newTotal = Optional.ofNullable(user.getTotalTraffic()).orElse(0L) + grantedTraffic;

        XrayUser update = new XrayUser();
        update.setId(user.getId());
        update.setTotalTraffic(newTotal);
        userMapper.updateXrayUser(update);
    }

    /**
     * 经销商政策：带邀请码注册的用户，首次充值成功额外赠送一份流量。
     * 只在该用户的第一笔成功订单上发放（此时订单刚被标记成功，成功单数正好是 1）。
     */
    private long firstChargeBonusTraffic(XrayUser user) {
        if (StringUtils.isBlank(user.getInviteCode())) {
            return 0L;
        }

        XrayPaymentOrder successQuery = new XrayPaymentOrder();
        successQuery.setUserId(user.getId());
        successQuery.setStatus("SUCCESS");
        if (orderMapper.selectXrayPaymentOrderList(successQuery).size() != 1) {
            return 0L;
        }

        List<XrayDistributorsConfig> configs =
                distributorsConfigMapper.selectXrayDistributorsConfigList(new XrayDistributorsConfig());
        if (configs == null || configs.isEmpty()) {
            return 0L;
        }
        long bonus = Optional.ofNullable(configs.get(0).getFirstChargeBonusTraffic()).orElse(0L);
        if (bonus > 0L) {
            log.info("first charge bonus traffic granted userId={} bytes={}", user.getId(), bonus);
        }
        return Math.max(0L, bonus);
    }

    @Override
    public void deleteOrderByOrderNo(String orderNo) {
        orderMapper.deleteXrayPaymentOrderByOrderNo(orderNo);
    }

    @Override
    public void updateOrder(XrayPaymentOrder update){
        orderMapper.updateXrayPaymentOrder(update);
    }

    @Scheduled(fixedDelay = 60000L, initialDelay = 60000L)
    public void closeExpiredPendingOrders() {
        int rows = orderMapper.closeExpiredPendingOrders();
        rows += submitTokenMapper.expireUnusedTokens();
        if (rows > 0) {
            log.info("Closed or expired {} payment records", rows);
        }
    }
}
