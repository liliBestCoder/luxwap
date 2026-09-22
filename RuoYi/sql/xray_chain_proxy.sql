-- ----------------------------
-- Table structure for xray_chain_proxy_node (链式代理上游端点表)
-- ----------------------------
DROP TABLE IF EXISTS `xray_chain_proxy_node`;
CREATE TABLE `xray_chain_proxy_node` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID（数据库内部物理主键）',
  `node_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '对外业务安全唯一Key（防遍历UUID/NanoID）',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '所属用户ID（0表示平台预设公共端点池，>0表示用户私有端点）',
  `source_type` tinyint NOT NULL DEFAULT '1' COMMENT '端点来源：1-用户自建导入，2-平台统一分发/商用住宅池',
  `protocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'socks5' COMMENT '代理协议：socks5, http, https',
  `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '代理主机（IPv4、IPv6或域名）',
  `port` int NOT NULL COMMENT '代理端口（1-65535）',
  `username` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '认证账号',
  `password_cipher` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '认证密码（AES-256-GCM 密文存储）',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户自定义备注',
  `exit_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '实际出网探测IP',
  `country_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '出口国家代码（如：US, HK, JP, TW）',
  `region` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '出口省份/州',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '出口城市',
  `alive_status` tinyint NOT NULL DEFAULT '0' COMMENT '存活状态：0-未检测，1-可用有效，2-不可达/异常',
  `latency_ms` int DEFAULT '0' COMMENT '探测延迟（毫秒）',
  `refresh_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '动态住宅代理刷新提取URL',
  `last_check_time` datetime DEFAULT NULL COMMENT '最近一次查活时间',
  `sort_order` int DEFAULT '0' COMMENT '排序权重（降序）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_node_key` (`node_key`),
  KEY `idx_user_status` (`user_id`, `alive_status`, `del_flag`),
  KEY `idx_host_port` (`host`, `port`),
  KEY `idx_country` (`country_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='链式代理上游端点表';

-- ----------------------------
-- Table structure for xray_chain_proxy_config (用户链式代理配置表)
-- ----------------------------
DROP TABLE IF EXISTS `xray_chain_proxy_config`;
CREATE TABLE `xray_chain_proxy_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `is_enabled` tinyint NOT NULL DEFAULT '0' COMMENT '是否启用链式代理：0-关闭，1-启用',
  `active_node_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '当前激活的上游跳板端点Key（关联 xray_chain_proxy_node.node_key）',
  `exit_line_id` bigint DEFAULT NULL COMMENT '绑定的落地出口线路ID（关联 vpn_lines.id）',
  `mode` varchar(32) NOT NULL DEFAULT 'fixed_exit' COMMENT '激活模式：fixed_exit(固定出口), auto_best(智能优选)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户链式代理配置表';
