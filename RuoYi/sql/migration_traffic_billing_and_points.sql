-- ============================================================================
-- 计费模型迁移：会员时长(月) -> 流量配额(字节)，并引入积分体系
--
-- 执行前务必备份 xray_user / xray_packet / xray_distributors_config。
-- 本脚本可重复执行（列已存在时手动跳过对应 ALTER 即可）。
-- ============================================================================

-- ---------------------------------------------------------------------------
-- 1. 套餐表：按流量售卖
-- ---------------------------------------------------------------------------
ALTER TABLE `xray_packet`
  ADD COLUMN `traffic_bytes` BIGINT NOT NULL DEFAULT 0 COMMENT '套餐流量(字节)' AFTER `bonus_months`,
  ADD COLUMN `bonus_traffic_bytes` BIGINT NOT NULL DEFAULT 0 COMMENT '赠送流量(字节)' AFTER `traffic_bytes`,
  ADD COLUMN `price_per_gb` DECIMAL(18,2) NOT NULL DEFAULT 0 COMMENT '每GB单价' AFTER `price_per_month`;

-- 老的按月套餐一律下架，但保留行本身：历史订单通过 packet_id 关联，
-- 删掉会让已支付账单显示不出套餐名。
UPDATE `xray_packet` SET `status` = 0 WHERE `traffic_bytes` = 0;

-- 上架流量套餐，价格与客户端充值页保持一致，按「1个月 = 100GB」折算。
INSERT INTO `xray_packet`
  (`name`, `duration_months`, `bonus_months`, `traffic_bytes`, `bonus_traffic_bytes`,
   `price`, `price_per_month`, `price_per_gb`, `description`, `status`, `sort_order`)
VALUES
  ('100GB',  0, 0,  100 * 1073741824, 0, 8.99,  8.99, 0.09,
   '按量付费，灵活自由<br>即刻到账，立即享受全部会员权益', 1, 10),
  ('300GB',  0, 0,  300 * 1073741824, 0, 23.97, 7.99, 0.08,
   '季度特惠，包含多国原生极速专线节点', 1, 20),
  ('1200GB', 0, 0, 1200 * 1073741824, 0, 71.88, 5.99, 0.06,
   '超值大流量，享受最高等级带宽与优先专属服务', 1, 30);

-- ---------------------------------------------------------------------------
-- 2. 用户表：流量配额取代到期日作为准入依据
-- ---------------------------------------------------------------------------
ALTER TABLE `xray_user`
  ADD COLUMN `total_traffic` BIGINT NOT NULL DEFAULT 0 COMMENT '累计流量配额(字节)，used_traffic 达到即停服' AFTER `used_traffic`;

-- 存量用户按剩余会员天数折算配额：剩余天数 / 30 * 100GB，已过期的给 0。
-- 已用流量要补回配额里，否则迁移当天所有老用户会因 used >= total 立刻断服。
UPDATE `xray_user`
SET `total_traffic` = COALESCE(`used_traffic`, 0) + CASE
      WHEN `expiration` IS NULL OR `expiration` <= NOW() THEN 0
      ELSE CEIL(DATEDIFF(`expiration`, NOW()) / 30 * 107374182400)
    END
WHERE `total_traffic` = 0;

-- ---------------------------------------------------------------------------
-- 3. 经销商配置：首充赠送由月数改为流量
-- ---------------------------------------------------------------------------
ALTER TABLE `xray_distributors_config`
  ADD COLUMN `first_charge_bonus_traffic` BIGINT NOT NULL DEFAULT 0 COMMENT '首充赠送流量(字节)' AFTER `first_charge_bonus`;

-- ---------------------------------------------------------------------------
-- 4. 积分体系
-- ---------------------------------------------------------------------------
DROP TABLE IF EXISTS `xray_user_point`;
CREATE TABLE `xray_user_point` (
  `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`         BIGINT      NOT NULL COMMENT '用户ID',
  `balance`         BIGINT      NOT NULL DEFAULT 0 COMMENT '当前可用积分',
  `total_earned`    BIGINT      NOT NULL DEFAULT 0 COMMENT '累计获得积分',
  `settled_traffic` BIGINT      NOT NULL DEFAULT 0 COMMENT '已结算过积分的流量水位(字节)',
  `created_at`      TIMESTAMP   NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`      TIMESTAMP   NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_point_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分账户';

-- 存量用户的水位直接对齐当前已用流量，避免迁移瞬间按历史流量一次性补发巨额积分。
INSERT INTO `xray_user_point` (`user_id`, `balance`, `total_earned`, `settled_traffic`)
SELECT `id`, 0, 0, COALESCE(`used_traffic`, 0) FROM `xray_user`;

DROP TABLE IF EXISTS `xray_point_record`;
CREATE TABLE `xray_point_record` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id`       BIGINT       NOT NULL COMMENT '用户ID',
  `change_amount` BIGINT       NOT NULL COMMENT '积分变动，正为获得负为消耗',
  `balance_after` BIGINT       NOT NULL COMMENT '变动后余额',
  `type`          VARCHAR(32)  NOT NULL COMMENT 'EARN_TRAFFIC 用量发放 / EXCHANGE_TRAFFIC 兑换流量',
  `remark`        VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_at`    TIMESTAMP    NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_point_record_user` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户积分流水';
