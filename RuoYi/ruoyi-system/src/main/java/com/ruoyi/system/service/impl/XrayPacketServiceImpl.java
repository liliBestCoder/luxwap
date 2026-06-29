package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.XrayPacket;
import com.ruoyi.system.mapper.XrayPacketMapper;
import com.ruoyi.system.service.IXrayPacketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class XrayPacketServiceImpl implements IXrayPacketService {

    @Resource
    private XrayPacketMapper packetMapper;

    @Override
    public List<XrayPacket> getPacketList() {
        return packetMapper.selectAllPackets();
    }

    @Override
    public List<XrayPacket> getEnabledPacketList() {
        return packetMapper.selectEnabledPackets();
    }

    @Override
    public XrayPacket selectPacketById(Long packetId) {
        return packetMapper.selectPacketById(packetId);
    }

    @Override
    public int savePacket(XrayPacket packet) {
        if (packet.getStatus() == null) {
            packet.setStatus(1);
        }
        if (packet.getSortOrder() == null) {
            packet.setSortOrder(0);
        }
        if (packet.getId() == null) {
            return packetMapper.insertPacket(packet);
        }
        return packetMapper.updatePacket(packet);
    }

    @Override
    public int deletePacketById(Long id) {
        return packetMapper.deletePacketById(id);
    }
}
