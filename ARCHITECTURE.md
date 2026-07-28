# AI 面试模拟器 — 系统架构

## 全系统架构

```
                         ┌──────────────┐
                         │   浏览器 Vue3  │
                         │   :5173       │
                         └──────┬───────┘
                                │ HTTP（所有请求）
                                ▼
                         ┌─────────────────────┐
                         │  Java Spring Boot   │
                         │  :8080              │
                         │                     │
                         │  /auth/*  注册登录   │
                         │  /users/* 用户管理   │
                         │  /ai/*    转发AI    │
                         └──┬──────┬──────┬───┘
                            │      │      │
                   ┌────────┘      │      └────────┐
                   ▼               ▼               ▼
            ┌──────────┐   ┌──────────────┐   ┌──────────┐
            │  Redis   │   │Python FastAPI│   │  MySQL   │
            │  :6379   │   │:8000         │   │  :3306   │
            │          │   │              │   │          │
            │ 存/验/删 │   │ 出题 追问 打分│   │ users    │
            │ JWT Token│   │ 查面试记录    │   │interviews│
            └──────────┘   └──────┬───────┘   └────▲─────┘
                                  │                 │
                                  ▼                 │
                           ┌──────────────┐         │
                           │  DeepSeek    │         │
                           │  大模型 API   │         │
                           └──────────────┘         │
                                                    │
                   Python 直连 MySQL ────────────────┘
                   （查面试记录、存打分结果）
```

## 谁连谁

| 连接 | 说明 |
|------|------|
| 浏览器 → Java | 所有请求统一入口 |
| Java → Redis | 登录存 Token，之后每次请求验 Token |
| Java → MySQL | 读写 users 表（MyBatis-Plus） |
| Java → Python | AI 请求转发，RestTemplate 调用 |
| Python → DeepSeek | 出题 / 追问 / 打分 |
| Python → MySQL | 读写 interviews 表（pymysql） |

## 数据流

```
登录：
浏览器 → Java → MySQL 查用户 → Redis 存 Token → 返回 Token 给浏览器

面试：
浏览器（带Token）→ Java → Redis 验 Token
                          → Python → DeepSeek → Python → MySQL 存记录
                          ← Python ← Java ← 浏览器
```

## 技术栈

| 层级 | 技术 | 端口 |
|------|------|:--:|
| 前端 | Vue3 + Element Plus + Vite | 5173 |
| 后端（业务） | Java 21 + Spring Boot 4 + MyBatis-Plus | 8080 |
| 后端（AI） | Python 3.11 + FastAPI + LangChain | 8000 |
| 数据库 | MySQL 8.0 | 3306 |
| 缓存 | Redis 7 | 6379 |
| AI API | DeepSeek v4 Pro | — |
| 认证 | JWT + Redis TokenStore | — |
| 部署 | Docker Compose | — |

## 项目结构

```
ai-interview-simulator/
├── frontend/               Vue3 + Element Plus
│   ├── src/views/
│   │   ├── Login.vue               登录页
│   │   ├── Home.vue                工作台
│   │   ├── InterviewCreate.vue     创建面试
│   │   ├── InterviewSession.vue    面试对话
│   │   └── InterviewHistory.vue    面试记录
│   └── Dockerfile
├── backend/                Java Spring Boot
│   ├── controller/
│   │   ├── AuthController.java     注册/登录/登出
│   │   ├── UserController.java     用户CRUD
│   │   └── AiController.java       网关转发Python
│   ├── util/
│   │   ├── JwtUtil.java            JWT生成/验证
│   │   └── TokenStore.java         Redis Token管理
│   └── Dockerfile
├── ai-service/             Python FastAPI
│   ├── main.py                     AI出题/追问/打分
│   ├── db.py                       数据库连接
│   └── Dockerfile
├── mysql-init/             MySQL初始化
│   └── init.sql                    建表脚本
├── docker-compose.yml      一键启动全部服务
├── ARCHITECTURE.md         本文件
└── README.md
```
