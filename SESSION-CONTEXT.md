# 会话上下文 — 新对话加载

> 上次会话日期：2026-08-06

---

## 一、做了什么

将 AI 面试模拟器从"骨架跑通、多处假数据"推进到**全链路真流程闭环**：

### 🔴 安全底线（3 项全部完成）
- **JWT Filter** — 新建 `JwtFilter.java`，拦截非 `/auth/*` 请求，验签名→过期→TokenStore，不合法 401
- **路由守卫** — `router/index.js` 加 `beforeEach`，未登录跳登录，白名单 Login/Register
- **输入校验** — 后端 AuthController 校验用户名非空不重复、密码≥6位、邮箱格式；前端密码 min 校验

### 🔴 体验闭环（4 项全部完成）
- **注册页** — 新建 `Register.vue`，真表单接 `/auth/register`，成功跳登录
- **首页** — `Home.vue` 动态问候+实时统计（面试次数/平均分）
- **面试历史** — `InterviewHistory.vue` 8 条假数据 → 真接口，分页，loading/error/empty 三态
- **创建面试** — `InterviewCreate.vue` setTimeout → `POST /interviews` + `/ai/generate-questions`，文件上传解析

### 🟡 工程素养（3 项全部完成）
- **全局异常处理** — 新建 `GlobalExceptionHandler.java`，`@RestControllerAdvice`
- **后端日志** — AuthController(登录/注册/登出)、AiController(AI调用耗时)、JwtFilter(Token验证失败)
- **错误处理三态** — 所有前端页面 loading/error+重试/empty

### 🟡 项目完整性（8 项完成 7 项）
- ✅ Swagger 接入（pom.xml 加 springdoc 依赖）
- ✅ 分页（后端 LIMIT/OFFSET + 前端 el-pagination）
- ✅ 前端 .env（VITE_API_BASE_URL，所有 fetch 用 `import.meta.env`）
- ✅ Docker 健康检查 + 版本锁定（mysql:8.0.35, redis:7.4.1-alpine）
- ✅ Flyway 数据库迁移（V1__init.sql）
- ✅ README 补 API 列表 + 架构图更新
- ✅ 文件上传+解析（FastAPI `/upload-document`，pypdf/python-docx）
- ⬜ Swagger 截图（需启动后截图）

### 其他修复
- **题号不推进** — 加 exchangeCount + AI 信号检测（【下一题】/【面试结束】）
- **面试记录不保存** — handleEnd 加 interview_id（从 route.params.id 取）
- **404 刷新** — nginx.conf SPA fallback（try_files $uri /index.html）+ no-cache
- **美化丢失** — 从 git commit b860a0f 恢复美化版文件，重新叠加功能代码

---

## 二、新建/修改的文件

| 文件 | 操作 |
|------|:--:|
| `backend/.../config/JwtFilter.java` | 新建 |
| `backend/.../config/GlobalExceptionHandler.java` | 新建 |
| `backend/.../config/MyBatisPlusConfig.java` | 新建 |
| `backend/.../controller/InterviewController.java` | 新建 |
| `backend/.../controller/AuthController.java` | 重写（加校验+日志+RegisterRequest） |
| `backend/.../controller/AiController.java` | 加日志+耗时 |
| `backend/.../service/UserService.java` | 加 findByUsername/existsByUsername |
| `backend/.../pom.xml` | 加 Swagger + Flyway |
| `backend/.../application.yml` | 加 Flyway 配置 |
| `backend/.../db/migration/V1__init.sql` | 新建 |
| `frontend/src/views/Register.vue` | 新建 |
| `frontend/src/views/Login.vue` | 密码校验 + register 路由 + env url |
| `frontend/src/views/Home.vue` | 动态问候+统计 |
| `frontend/src/views/InterviewHistory.vue` | 真数据+分页+三态 |
| `frontend/src/views/InterviewCreate.vue` | 真接口+文件上传 |
| `frontend/src/views/InterviewSession.vue` | 题号追踪+interview_id |
| `frontend/src/layout/MainLayout.vue` | 动态用户名+真登出 |
| `frontend/src/router/index.js` | /register 路由+beforeEach 守卫 |
| `frontend/nginx.conf` | 新建（SPA fallback + no-cache） |
| `frontend/Dockerfile` | 加 nginx.conf |
| `frontend/.env` | 新建 VITE_API_BASE_URL |
| `ai-service/main.py` | /upload-document 端点 + chat system prompt |
| `ai-service/requirements.txt` | 加 pypdf/python-docx/python-multipart |
| `docker-compose.yml` | 健康检查+版本锁定 |
| `README.md` | API 列表+架构更新 |

---

## 三、当前运行状态

- 全部服务通过 `docker compose up -d --build` 运行
- 前端：http://localhost:5173
- 后端：http://localhost:8080，Swagger：http://localhost:8080/swagger-ui.html
- Python AI：http://localhost:8000
- 已验证：所有路由 200，no-cache 头生效

---

## 四、关键约束（来自 career-plan.md / user-feedback.md）

- 用户称呼：丢丢
- 说"上课"= SESSION-START.md 防线零 + SQL 30min + 五步法
- 五步法：讲→演示→用户讲→口述打字→出题考，Step 3 不可跳
- SQL 铁规：每天 10 道，不标难度，一题一题出
- 用户信号：锚/慢/闭嘴/存

---

## 五、ROADMAP 剩余任务

教学部分（W1-5 到 W4-7）和交付物（演示视频、博客），见 `PROJECT-ROADMAP.md`。
