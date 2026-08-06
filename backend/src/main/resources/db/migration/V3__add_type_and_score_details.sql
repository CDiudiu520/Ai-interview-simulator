-- V3：给 interviews 表补 type + 评分维度（highlights/weaknesses/suggestions）
-- 背景：历史页 type 写死 'tech'（表里没有 type 字段）；
--       评分时 AI 返回的高亮/短板/建议三个数组没有持久化，详情页无法回看。
-- 本迁移幂等：列已存在就跳过。
SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'interviews'
      AND COLUMN_NAME = 'type'
);
SET @ddl = IF(@col_exists = 0,
    'ALTER TABLE interviews ADD COLUMN type VARCHAR(20) DEFAULT ''tech'' AFTER position',
    'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'interviews'
      AND COLUMN_NAME = 'highlights'
);
SET @ddl = IF(@col_exists = 0,
    'ALTER TABLE interviews ADD COLUMN highlights TEXT DEFAULT NULL AFTER feedback',
    'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'interviews'
      AND COLUMN_NAME = 'weaknesses'
);
SET @ddl = IF(@col_exists = 0,
    'ALTER TABLE interviews ADD COLUMN weaknesses TEXT DEFAULT NULL AFTER highlights',
    'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'interviews'
      AND COLUMN_NAME = 'suggestions'
);
SET @ddl = IF(@col_exists = 0,
    'ALTER TABLE interviews ADD COLUMN suggestions TEXT DEFAULT NULL AFTER weaknesses',
    'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
