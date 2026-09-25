-- ----------------------------
-- Table structure for xray_feedback (用户意见与问题反馈表)
-- ----------------------------
DROP TABLE IF EXISTS `xray_feedback`;
CREATE TABLE `xray_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '提交用户ID',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号/邮箱',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'suggestion' COMMENT '反馈类型：suggestion(功能建议), bug(缺陷故障), speed(速度线路), other(其他)',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '反馈标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '反馈详情描述',
  `contact` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户预留联系方式(QQ/微信/邮箱)',
  `app_version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '客户端版本号(如 2.5.0)',
  `platform` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作系统平台(Windows, macOS等)',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-待处理, 1-处理中, 2-已解决, 3-已驳回/忽略',
  `reply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '运营人员回复内容',
  `reply_time` datetime DEFAULT NULL COMMENT '回复时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type_status` (`type`, `status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户意见与问题反馈表';
