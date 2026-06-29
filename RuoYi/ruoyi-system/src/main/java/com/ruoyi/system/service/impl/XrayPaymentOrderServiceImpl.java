package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.XrayPacket;
import com.ruoyi.system.domain.XrayPaymentNotifyLog;
import com.ruoyi.system.domain.XrayPaymentOrder;
import com.ruoyi.system.domain.XrayUser;
import com.ruoyi.system.mapper.XrayPacketMapper;
import com.ruoyi.system.mapper.XrayPaymentNotifyLogMapper;
import com.ruoyi.system.mapper.XrayPaymentOrderMapper;
import com.ruoyi.system.mapper.XrayPaymentSubmitTokenMapper;
import com.ruoyi.system.mapper.XrayUserMapper;
import com.ruoyi.system.service.IXrayPaymentOrderService;
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

        int months = Optional.ofNullable(packet.getDurationMonths()).orElse(0)
                + Optional.ofNullable(packet.getBonusMonths()).orElse(0);
        if (months <= 0) {
            throw new IllegalStateException("packet duration is invalid");
        }

        Date now = new Date();
        Date currentExpiration = user.getExpiration();
        Date base = currentExpiration != null && currentExpiration.after(now) ? currentExpiration : now;
        LocalDateTime newExpiration = LocalDateTime.ofInstant(base.toInstant(), ZoneId.systemDefault()).plusMonths(months);

        XrayUser update = new XrayUser();
        update.setId(user.getId());
        update.setExpiration(Date.from(newExpiration.atZone(ZoneId.systemDefault()).toInstant()));
        update.setCumulativeMonths(Optional.ofNullable(user.getCumulativeMonths()).orElse(0) + months);
        userMapper.updateXrayUser(update);
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
