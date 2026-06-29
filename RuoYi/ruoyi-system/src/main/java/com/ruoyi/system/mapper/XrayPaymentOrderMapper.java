package com.ruoyi.system.mapper;



import com.ruoyi.system.domain.XrayPaymentOrder;

import org.apache.ibatis.annotations.Mapper;



import java.util.List;



@Mapper

public interface XrayPaymentOrderMapper {



    // 

    void insertXrayPaymentOrder(XrayPaymentOrder order);



    // 

    List<XrayPaymentOrder> selectXrayPaymentOrderList(XrayPaymentOrder order);



    XrayPaymentOrder selectXrayPaymentOrderByOrderNo(String orderNo);



    XrayPaymentOrder selectReusablePendingOrder(XrayPaymentOrder order);



    void updateXrayPaymentOrderStatus(XrayPaymentOrder order);



    int markOrderSuccessIfNotPaid(XrayPaymentOrder order);



    void deleteXrayPaymentOrderByOrderNo(String orderNo);



    int updateXrayPaymentOrder(XrayPaymentOrder order);



    int closeExpiredPendingOrders();

    XrayPaymentOrder selectXrayPaymentOrderBySubmitToken(String submitToken);

}

