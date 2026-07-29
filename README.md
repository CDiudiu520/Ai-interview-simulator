# AI 面试模拟器

> 输入 JD，AI 自动出题 + 追问 + 打分。帮你面试前模拟实战。

[![技术栈](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-green)](https://spring.io/)
[![Python](https://img.shields.io/badge/Python-3.11-blue)](https://www.python.org/)
[![Vue](https://img.shields.io/badge/Vue-3-brightgreen)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-✓-2496ED)](https://www.docker.com/)

## 为什么做这个项目

一个展示全栈 + AI 工程能力的综合项目：前端 Vue3 + 后端 Java Spring Boot 微服务 + Python LangChain AI 服务，Docker Compose 五服务编排，一键本地启动。覆盖认证授权、RAG 知识库、多轮对话 Agent、结构化评分等 AI 应用开发核心场景。

## 功能

- **AI 自动出题** — 输入 JD，DeepSeek 生成 5 道面试题，每题标注考察点
- **多轮追问** — AI 根据你的回答深入追问，模拟真实面试节奏
- **智能打分** — 面试结束后 AI 打分（0-100）+ 评语
- **面试记录** — 历史面试可搜索、可回顾
- **用户系统** — JWT 注册登录，Redis 管理会话

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue3 + Element Plus + Vite |
| 后端（业务） | Java 21 + Spring Boot 4 + MyBatis-Plus |
| 后端（AI） | Python 3.11 + FastAPI + LangChain |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7 |
| AI | DeepSeek v4 Pro |
| 认证 | JWT |
| 部署 | Docker Compose 一键启动 |

## 快速开始

```bash
# 1. 克隆项目
git clone https://github.com/CDiudiu520/Ai-interview-simulator.git
cd Ai-interview-simulator

# 2. 配置 API Key
echo "DEEPSEEK_API_KEY=你的key" > .env

# 3. 一键启动
docker compose up

# 4. 浏览器打开
# http://localhost:5173
```

## 系统架构

详见 [ARCHITECTURE.md](ARCHITECTURE.md)

```
浏览器(Vue3) → Java(Spring Boot) → Python(FastAPI) → DeepSeek
                  ↓      ↘              ↓
               Redis    MySQL ←─────────┘
              (Token)  (用户+面试记录)
```

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

## 演示

本地一键启动：`docker compose up`，浏览器打开 `http://localhost:5173`

## License

MIT
