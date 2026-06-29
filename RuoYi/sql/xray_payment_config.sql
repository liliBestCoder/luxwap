SET @sql := IF((SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'xray_packet' AND COLUMN_NAME = 'status') = 0,
  'ALTER TABLE xray_packet ADD COLUMN status TINYINT DEFAULT 1',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'xray_packet' AND COLUMN_NAME = 'sort_order') = 0,
  'ALTER TABLE xray_packet ADD COLUMN sort_order INT DEFAULT 0',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS xray_payment_order (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  packet_id BIGINT NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  type VARCHAR(20) NOT NULL,
  amount DECIMAL(18,2) NOT NULL,
  currency VARCHAR(16) NOT NULL DEFAULT 'USD',
  status VARCHAR(32) NOT NULL,
  notify_url VARCHAR(255) NULL,
  return_url VARCHAR(255) NULL,
  payment_url TEXT NULL,
  trade_no VARCHAR(128) NULL,
  paid_amount DECIMAL(18,2) NULL,
  paid_currency VARCHAR(16) NULL,
  paid_at DATETIME NULL,
  success_at DATETIME NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  expired_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_order_no (order_no),
  KEY idx_user_id (user_id),
  KEY idx_packet_id (packet_id),
  KEY idx_status (status),
  KEY idx_trade_no (trade_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET @sql := IF((SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'xray_payment_order' AND COLUMN_NAME = 'trade_no') = 0,
  'ALTER TABLE xray_payment_order ADD COLUMN trade_no VARCHAR(128) NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'xray_payment_order' AND COLUMN_NAME = 'currency') = 0,
  'ALTER TABLE xray_payment_order ADD COLUMN currency VARCHAR(16) NOT NULL DEFAULT ''USD'' AFTER amount',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'xray_payment_order' AND COLUMN_NAME = 'paid_amount') = 0,
  'ALTER TABLE xray_payment_order ADD COLUMN paid_amount DECIMAL(18,2) NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

ALTER TABLE xray_payment_order MODIFY amount DECIMAL(18,2) NOT NULL;
ALTER TABLE xray_payment_order MODIFY paid_amount DECIMAL(18,2) NULL;

SET @sql := IF((SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'xray_payment_order' AND COLUMN_NAME = 'paid_currency') = 0,
  'ALTER TABLE xray_payment_order ADD COLUMN paid_currency VARCHAR(16) NULL AFTER paid_amount',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'xray_payment_order' AND COLUMN_NAME = 'paid_at') = 0,
  'ALTER TABLE xray_payment_order ADD COLUMN paid_at DATETIME NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(1) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'xray_payment_order' AND COLUMN_NAME = 'success_at') = 0,
  'ALTER TABLE xray_payment_order ADD COLUMN success_at DATETIME NULL',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF((SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'xray_payment_order' AND INDEX_NAME = 'idx_trade_no') = 0,
  'ALTER TABLE xray_payment_order ADD KEY idx_trade_no (trade_no)',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS xray_payment_notify_log (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_no VARCHAR(64) NULL,
  trade_no VARCHAR(128) NULL,
  notify_id VARCHAR(128) NULL,
  channel VARCHAR(32) NOT NULL,
  trade_status VARCHAR(64) NULL,
  amount DECIMAL(18,2) NULL,
  currency VARCHAR(16) NULL,
  raw_payload TEXT NULL,
  sign_verified TINYINT DEFAULT 0,
  handled TINYINT DEFAULT 0,
  fail_reason VARCHAR(500) NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_order_no (order_no),
  KEY idx_trade_no (trade_no),
  KEY idx_notify_id (notify_id),
  KEY idx_channel_created_at (channel, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE xray_payment_notify_log MODIFY amount DECIMAL(18,2) NULL;

INSERT INTO xray_packet (name, duration_months, bonus_months, price, price_per_month, description, status, sort_order)
SELECT '1个月', 1, 0, 8.99, 8.99, '按月付费，灵活自由<br>即时开通，立即享受全会员权益', 1, 10
WHERE NOT EXISTS (SELECT 1 FROM xray_packet WHERE duration_months = 1 AND bonus_months = 0);

INSERT INTO xray_packet (name, duration_months, bonus_months, price, price_per_month, description, status, sort_order)
SELECT '12个月', 12, 3, 59.85, 3.99, '一次性快速长期续订，赠送 3 个月时长<br>即时开通，立即享受全会员权益', 1, 20
WHERE NOT EXISTS (SELECT 1 FROM xray_packet WHERE duration_months = 12 AND bonus_months = 3);

INSERT INTO xray_packet (name, duration_months, bonus_months, price, price_per_month, description, status, sort_order)
SELECT '24个月', 24, 3, 61.83, 2.29, '一次性最长周期优惠，赠送 3 个月时长<br>即时开通，立即享受全会员权益', 1, 30
WHERE NOT EXISTS (SELECT 1 FROM xray_packet WHERE duration_months = 24 AND bonus_months = 3);

INSERT INTO xray_payment_merchant (name, type, status, config, created_at, updated_at)
SELECT 'Alipay International Payment', 'alipay', 1,
       '{"appId":"demo-app-id","privateKey":"demo-private-key","alipayPublicKey":"demo-public-key","notifyUrl":"http://127.0.0.1:8081/api/client/payment/notify/alipay","serviceUrl":"https://openapi.alipay.com/gateway.do","format":"json","charset":"UTF-8","signType":"RSA2"}',
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM xray_payment_merchant WHERE type = 'alipay');

INSERT INTO xray_payment_merchant (name, type, status, config, created_at, updated_at)
SELECT 'WeChat International Payment', 'wechat', 1,
       '{"appId":"demo-app-id","mchId":"demo-mch-id","apiKey":"demo-api-key","notifyUrl":"http://127.0.0.1:8081/api/client/payment/notify/wechat"}',
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM xray_payment_merchant WHERE type = 'wechat');

INSERT INTO xray_payment_merchant (name, type, status, config, created_at, updated_at)
SELECT 'Stripe Credit Card Payment', 'stripe', 1,
       '{"secretKey":"sk_test_demo","publishableKey":"pk_test_demo"}',
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM xray_payment_merchant WHERE type = 'stripe');

INSERT INTO xray_payment_merchant (name, type, status, config, created_at, updated_at)
SELECT 'PayPal Payment', 'paypal', 1,
       '{"clientId":"demo-client-id","clientSecret":"demo-client-secret"}',
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM xray_payment_merchant WHERE type = 'paypal');

INSERT INTO xray_payment_merchant (name, type, status, config, created_at, updated_at)
SELECT 'Circle Payment', 'circle', 1,
       '{"apiKey":"demo-api-key","accountId":"demo-account-id"}',
       NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM xray_payment_merchant WHERE type = 'circle');
