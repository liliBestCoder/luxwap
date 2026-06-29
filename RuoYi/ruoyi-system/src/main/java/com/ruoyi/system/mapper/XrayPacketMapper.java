package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.XrayPacket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XrayPacketMapper {
    List<XrayPacket> selectAllPackets();
    List<XrayPacket> selectEnabledPackets();
    XrayPacket selectPacketById(@Param("id") Long id);
    int insertPacket(XrayPacket packet);
    int updatePacket(XrayPacket packet);
    int deletePacketById(@Param("id") Long id);
}
