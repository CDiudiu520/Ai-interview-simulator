# AI 面试模拟器

> 上传 JD 和面经，AI 自动出题、逐题追问、结构化打分。面试前的 AI 陪练。

[![技术栈](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-green)](https://spring.io/)
[![Python](https://img.shields.io/badge/Python-3.11-blue)](https://www.python.org/)
[![Vue](https://img.shields.io/badge/Vue-3-brightgreen)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-✓-2496ED)](https://www.docker.com/)

---

## 核心亮点

### 🧠 RAG 知识库 — 面经出题
上传你的面经/简历文档，AI 会**基于你的材料出题**，而不是千篇一律的通用题。文档自动分块、向量化、检索，出题时只取最相关的片段拼进 Prompt。

### 🤖 LangGraph Agent — 状态化面试
面试的**状态（第几题、追问几轮、何时换题、何时结束）由后端 Agent 管理**，不是前端死记。AI 会针对你的回答深入追问，每题最多追问 3 轮（代码兜底，不会永远纠缠一道题）。

### 📊 结构化评分报告
面试结束后，AI 从 **0-100 打分 + 亮点 + 短板 + 改进建议** 四个维度输出结构化反馈，历史记录可随时回看完整报告。

### 🔐 完整工程闭环
JWT 认证 + 路由守卫 + 前端路由 + 后端 Filter 双重保护，全局异常处理，Flyway 数据库迁移，Docker Compose 五服务一键启动。

---

## 快速开始

```bash
# 1. 克隆项目
git clone https://github.com/CDiudiu520/Ai-interview-simulator.git
cd Ai-interview-simulator

# 2. 配置 DeepSeek API Key
echo "DEEPSEEK_API_KEY=你的key" > .env

# 3. 一键启动（5 个服务：前端/后端/AI/MySQL/Redis）
docker compose up -d --build

# 4. 浏览器打开
# http://localhost:5173
```

> 首次启动会自动建表（Flyway 迁移 + init.sql），无需手动操作数据库。

---

## 功能一览

| 功能 | 说明 |
|------|------|
| 用户系统 | 注册 / 登录 / 登出，JWT 认证，BCrypt 密码加密 |
| 创建面试 | 填 JD + 上传简历/面经（PDF/Word/TXT），AI 生成题目 |
| RAG 知识库 | 文档分块 → 向量化 → 检索，出题引用材料内容 |
| AI 对话 | LangGraph Agent 管理状态，自动追问、换题、结束 |
| 智能评分 | 0-100 分 + 亮点 + 短板 + 改进建议 |
| 面试记录 | 历史列表 + 分页 + 搜索 + 完整评分报告回看 |
| 重新模拟 | 一键用相同岗位重开一场面试 |

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue3 + Element Plus + Vite |
| 后端（业务） | Java 21 + Spring Boot 4 + MyBatis-Plus |
| 后端（AI） | Python 3.11 + FastAPI + LangChain + LangGraph |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7（预留，会话当前用内存 TokenStore）|
| AI | DeepSeek v4 Pro |
| 认证 | JWT |
| 部署 | Docker Compose 一键启动 |

---

## 系统架构

详见 [ARCHITECTURE.md](ARCHITECTURE.md)

```
浏览器(Vue3 :5173)
   │  HTTP（带 JWT）
   ▼
Java Spring Boot (:8080)
   ├─ /auth/*     注册/登录/登出
   ├─ /interviews 创建/列表/详情
   └─ /ai/*       ──转发──▶ Python FastAPI (:8000)
                              ├─ /generate-questions  RAG出题
                              ├─ /chat               LangGraph Agent
                              ├─ /evaluate           结构化评分
                              └─ /upload-document    文档解析→RAG索引
                                    │
                              DeepSeek API
```

**数据流：**
```
注册 → MySQL 存用户 → 生成 JWT → TokenStore(内存)
创建面试 → POST /interviews → 调 RAG 检索面经 → AI 出题
面试对话 → /ai/chat → LangGraph 管状态 → 追问/换题/结束
面试结束 → /ai/evaluate → 打分+亮点/短板/建议 → 存 DB → 历史页可回看
```

---

## API 接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|:--:|
| POST | `/auth/register` | 用户注册 | — |
| POST | `/auth/login` | 用户登录 | — |
| POST | `/auth/logout` | 用户登出 | Bearer |
| GET | `/interviews` | 面试列表（分页） | Bearer |
| GET | `/interviews/{id}` | 面试详情（评分报告） | Bearer |
| POST | `/interviews` | 创建面试 | Bearer |
| GET | `/interviews/stats` | 面试统计 | Bearer |
| POST | `/ai/generate-questions` | AI 生成题目（可带 document_id） | Bearer |
| POST | `/ai/chat` | AI 追问对话（LangGraph Agent） | Bearer |
| POST | `/ai/evaluate` | AI 评分 | Bearer |
| POST | `/upload-document`（AI服务 :8000） | 文档解析 + RAG 建索引 | — |

> 启动后访问 `http://localhost:8080/swagger-ui.html` 查看 Swagger 接口文档。

---

## 项目结构

```
ai-interview-simulator/
├── frontend/                  Vue3 + Element Plus
│   └── src/views/
│       ├── Login.vue          登录
│       ├── Register.vue       注册
│       ├── Home.vue           工作台
│       ├── InterviewCreate.vue 创建面试（上传面经）
│       ├── InterviewSession.vue AI 面试对话
│       ├── InterviewHistory.vue 面试记录
│       └── InterviewDetail.vue  评分报告详情
├── backend/                   Java Spring Boot
│   ├── config/                JwtFilter / Cors / 全局异常
│   ├── controller/            Auth / Interview / Ai / Health
│   ├── entity/                User / Interview
│   └── db/migration/          Flyway 迁移（V1/V2/V3）
├── ai-service/                Python FastAPI
│   ├── main.py                AI 出题/对话/评分/上传
│   ├── rag.py                 RAG：分块/向量化/检索
│   └── agent.py               LangGraph Agent 状态机
├── mysql-init/                MySQL 初始化建表
├── docker-compose.yml         五服务一键编排
└── ARCHITECTURE.md            架构文档
```

---

## 本地开发（不用 Docker）

```bash
# 1. 启动 MySQL & Redis（本机安装）
# 2. 启动 AI 服务
cd ai-service
pip install -r requirements.txt
uvicorn main:app --reload --port 8000

# 3. 启动 Java 后端
cd backend
mvn spring-boot:run

# 4. 启动前端
cd frontend
npm install && npm run dev
```

---

## License

MIT
