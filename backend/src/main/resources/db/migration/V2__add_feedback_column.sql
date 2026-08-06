-- V2：给 interviews 表补 feedback 列
-- 背景：早期版本的 init.sql 建表时没有 feedback，老库缺列导致查询崩 500。
-- 本迁移写成幂等的：列已存在就跳过（新库由 V1/init.sql 建表时已有），不存在才补。
SET @col_exists = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'interviews'
      AND COLUMN_NAME = 'feedback'
);
SET @ddl = IF(@col_exists = 0,
    'ALTER TABLE interviews ADD COLUMN feedback TEXT DEFAULT NULL AFTER score',
    'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
