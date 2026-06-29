package com.ruoyi.system.service;

import com.ruoyi.system.domain.XrayPacket;

import java.util.List;

public interface IXrayPacketService {
    List<XrayPacket> getPacketList();
    List<XrayPacket> getEnabledPacketList();
    public XrayPacket selectPacketById(Long packetId);
    int savePacket(XrayPacket packet);
    int deletePacketById(Long id);
}
