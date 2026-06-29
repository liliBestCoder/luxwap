package com.ruoyi.system.service;

import com.ruoyi.system.domain.XrayPaymentOrder;
import com.ruoyi.system.domain.XrayPaymentNotifyLog;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IXrayPaymentOrderService {

    void createOrder(XrayPaymentOrder order);

    void createOrderWithSubmitToken(XrayPaymentOrder order, String submitToken);

    List<XrayPaymentOrder> getOrders(XrayPaymentOrder order);

    XrayPaymentOrder getOrderByOrderNo(String orderNo);

    XrayPaymentOrder getOrderBySubmitToken(String submitToken);

    XrayPaymentOrder getReusablePendingOrder(XrayPaymentOrder order);

    void updateOrderStatus(String orderNo, String status);

    boolean markSuccessAndActivate(XrayPaymentOrder order, XrayPaymentNotifyLog log, String tradeNo, BigDecimal paidAmount, String paidCurrency, Date paidAt);

    void saveNotifyLog(XrayPaymentNotifyLog log);

    void deleteOrderByOrderNo(String orderNo);

    void updateOrder(XrayPaymentOrder update);
}
