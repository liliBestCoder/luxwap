package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.XrayPaymentNotifyLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface XrayPaymentNotifyLogMapper {
    int insertNotifyLog(XrayPaymentNotifyLog log);

    int markHandled(@Param("id") Long id);

    int markFailed(@Param("id") Long id, @Param("failReason") String failReason);
}
