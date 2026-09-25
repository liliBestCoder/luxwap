package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.XrayNodeCommand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 节点下发指令发件箱Mapper
 */
public interface XrayNodeCommandMapper {

    @Insert("INSERT INTO xray_node_command (msg_id, client_ip, command_type, payload, status, retry_count, next_retry_time, created_at, updated_at) " +
            "VALUES (#{msgId}, #{clientIp}, #{commandType}, #{payload}, 0, 0, NOW(), NOW(), NOW())")
    int insertCommand(XrayNodeCommand command);

    @Update("UPDATE xray_node_command SET status = 1, updated_at = NOW() WHERE msg_id = #{msgId} AND client_ip = #{clientIp}")
    int markAcked(@Param("msgId") String msgId, @Param("clientIp") String clientIp);

    @Select("SELECT id, msg_id as msgId, client_ip as clientIp, command_type as commandType, payload, status, retry_count as retryCount, next_retry_time as nextRetryTime " +
            "FROM xray_node_command WHERE client_ip = #{clientIp} AND status = 0 ORDER BY id ASC")
    List<XrayNodeCommand> selectPendingByClientIp(@Param("clientIp") String clientIp);

    @Select("SELECT id, msg_id as msgId, client_ip as clientIp, command_type as commandType, payload, status, retry_count as retryCount, next_retry_time as nextRetryTime " +
            "FROM xray_node_command WHERE status = 0 AND next_retry_time <= NOW() AND retry_count < 5 ORDER BY id ASC LIMIT 50")
    List<XrayNodeCommand> selectPendingForRetry();

    @Update("UPDATE xray_node_command SET retry_count = retry_count + 1, next_retry_time = DATE_ADD(NOW(), INTERVAL 5 SECOND), updated_at = NOW() WHERE id = #{id}")
    int updateRetry(@Param("id") Long id);

    @Update("UPDATE xray_node_command SET status = 2, updated_at = NOW() WHERE id = #{id}")
    int markFailed(@Param("id") Long id);

    @org.apache.ibatis.annotations.Delete("DELETE FROM xray_node_command WHERE status = 1 AND updated_at < DATE_SUB(NOW(), INTERVAL 7 DAY)")
    int cleanExpiredCommands();
}
