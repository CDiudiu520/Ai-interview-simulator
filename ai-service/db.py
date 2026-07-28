import os
import pymysql

# 数据库连接信息（优先用环境变量，没有就用本地 localhost）
DB_CONFIG = {
    "host": os.getenv("DB_HOST", "localhost"),
    "user": os.getenv("DB_USER", "root"),
    "password": os.getenv("DB_PASSWORD", "123456"),
    "database": os.getenv("DB_NAME", "ai_interview"),
    "charset": "utf8mb4",
}


def get_connection():
    """获取数据库连接"""
    return pymysql.connect(**DB_CONFIG)


def fetch_all(sql, params=None):
    """执行查询，返回所有结果（SELECT 用）"""
    conn = get_connection()
    try:
        cursor = conn.cursor(pymysql.cursors.DictCursor)
        cursor.execute(sql, params)
        return cursor.fetchall()
    finally:
        conn.close()


def execute(sql, params=None):
    """执行增删改（INSERT / UPDATE / DELETE 用）"""
    conn = get_connection()
    try:
        cursor = conn.cursor()
        cursor.execute(sql, params)
        conn.commit()
        return cursor.lastrowid
    finally:
        conn.close()
