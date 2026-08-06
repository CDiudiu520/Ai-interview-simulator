# AI 面试模拟器 · 完整项目清单

> 本文件是项目的完整待办清单。新对话中先读本文件，再读"必读文件列表"中的全部文件。

---

## 一、新对话必读文件

按顺序加载：

- [ ] `E:\claude lab\career-plan.md` — 求职路线：方向、项目定位、技术栈、时间表
- [ ] `C:\Users\苹果与飞刀男\.claude\projects\E--claude-lab\memory\learning-progress.md` — 学习进度：已完成内容、SQL 累计、薄弱点
- [ ] `C:\Users\苹果与飞刀男\.claude\projects\E--claude-lab\memory\user-feedback.md` — 教学纪律：五步法、锚点、SQL 规则、节奏信号
- [ ] `C:\Users\苹果与飞刀男\.claude\projects\E--claude-lab\memory\sql-practice-rules.md` — SQL 出题分配、错题追踪、总结格式
- [ ] `E:\claude lab\ai-interview-simulator\SESSION-START.md` — 防线零清单：上课触发 = 三行验证 + SQL 30min + 五步法
- [ ] `E:\claude lab\ai-interview-simulator\TEACHING-PLAN.md` — 8 月课表：W1~W4 每节锚点、过关标准、动手任务
- [ ] `E:\claude lab\ai-interview-simulator\PROJECT-ROADMAP.md` — 本文件

---

## 二、当前进度快照

**最新进展（截至 2026-08-05）：**
- W1-1 ✅ AuthController 三接口走读 + Login.vue setTimeout→fetch
- W1-2 ✅ JwtUtil + TokenStore + BCrypt
- W1-3 ✅ AiController 网关转发 + Interview 实体/Mapper + evaluate 存 DB
- W1-4 ✅ main.py 三条 AI 链路走读 + /evaluate Prompt 改造（结构化反馈） + 前端评分面板动态化
- W1-5 🔜 前端数据流：Login → Home → Create → Session
- W1-6 🔲 闸门验收

**当前弱项：**
- SQL GROUP BY 什么时候需要/不需要 —— 持续薄弱点
- SQL LEFT JOIN + COUNT NULL 处理
- SQL UNION + COALESCE

---

## 三、完整任务清单

### 🔴 安全底线（优先级最高）

- [ ] **JWT 验证 Filter** — 新建 `JwtFilter.java`，拦截除 `/auth/*` 外的所有请求，验 Token（签名 → 过期 → TokenStore），不合法返回 401
- [ ] **路由守卫** — 前端 `router/index.js` 加 `beforeEach`，未登录跳转登录页，白名单：Login/Register
- [ ] **输入校验** — 注册/登录前后端校验：用户名非空且不重复、密码 ≥ 6 位、邮箱格式

---

### 🔴 体验闭环（假数据 → 真流程）

- [ ] **注册页** — `Register.vue` 占位文字 → 真表单（用户名/密码/邮箱），接 `/auth/register`，注册成功跳登录页
- [ ] **首页** — `Home.vue` 写死数据 → 调后端统计接口（面试次数/平均分/最近趋势），动态渲染
- [ ] **面试历史** — `InterviewHistory.vue` 8 条假数据 → 调 `/interviews` 接口查 DB 真数据，按钮（查看详情/重新模拟）生效
- [ ] **创建面试** — `CreateInterview.vue` 表单提交 → 调后端接口 INSERT 到 interviews 表 → 跳转面试页

---

### 🟡 工程素养

- [ ] **错误处理三态** — 所有前端 API 调用补：loading（加载中）、error（失败提示+重试按钮）、empty（无数据占位图）
- [ ] **后端日志** — 关键操作记录：登录/注册/Token 验证失败/AI 调用耗时/异常堆栈
- [ ] **全局异常处理** — Spring Boot `@ControllerAdvice` 统一拦截异常，返回标准错误 JSON，删掉接口里的零散 try-catch

---

### 🟡 项目完整性

- [ ] **API 文档** — 接入 Springdoc/Swagger，每个接口加 `@Operation(summary="...")`，README 放 Swagger 截图
- [ ] **文件上传 + 解析** — FastAPI 新路由 `/upload-document`，接收 PDF/Word → pypdf 读文本 → 返回文本内容（为 RAG 做前置基础）
- [ ] **健康检查** — `docker-compose.yml` 五个服务加 `healthcheck`，确保 `docker ps` 能看到服务真实状态
- [ ] **分页** — 面试历史列表加分页（前端 `el-pagination` + 后端 PageHelper 或手动 LIMIT/OFFSET）
- [ ] **数据库迁移** — 引入 Flyway，`init.sql` 内容拆分到版本化迁移文件，表结构变更可追溯
- [ ] **前端环境变量** — API 地址 `127.0.0.1:8080` 抽离为 `.env`，Vite 通过 `import.meta.env` 读取
- [ ] **Docker 镜像版本锁定** — `docker-compose.yml` 中所有 `image` 固定小版本号（如 `mysql:8.0.35`）
- [ ] **README 完善** — 当前有架构图和启动命令，需补：接口列表（Swagger 截图）、技术栈亮点说明、项目 GIF 动图

---

### 🔵 W1 · 代码走读 + 前端数据流（教学部分）

- [ ] **W1-5** — 前端数据流：Login → Home → Create → Session，route query 传参 vs Pinia 全局状态
- [ ] **W1-6** — 闸门验收：① 随机抽 3 段代码讲数据流 ② SQL 综合 10 道（8 道以上过关）③ 口述全链路：注册→面试→评分

---

### 🟣 W2 · RAG 知识库（教学部分）

- [ ] **W2-1** — RAG 原理：文档→分块→Embedding→向量库→检索→拼 Prompt
- [ ] **W2-2** — 文档上传 + 分块（RecursiveCharacterTextSplitter）+ Embedding（调 DeepSeek API）
- [ ] **W2-3** — Chroma 存储 + 检索（余弦相似度）
- [ ] **W2-4** — 拼 Prompt + 联调（Token 管理、截断策略）
- [ ] **W2-5** — 全链路 + 我埋错你找（5 道）
- [ ] **W2-6** — 闸门验收：口述全过程 + 走读代码 + SQL 10 道

---

### 🟠 W3 · LangGraph Agent（教学部分）

- [ ] **W3-1** — Agent 原理：状态机 vs 链式 vs 路由式
- [ ] **W3-2** — LangGraph State 定义（current_question / history / follow_up_count）
- [ ] **W3-3** — 追问决策 + 换题逻辑（should_follow_up + max_follow_ups 兜底）
- [ ] **W3-4** — 上下文管理（滑动窗口 vs 摘要）
- [ ] **W3-5** — Agent 全链路 + 我埋错你找（5 道）
- [ ] **W3-6** — 闸门验收：口述 State 流转 + 走读代码 + SQL 10 道

---

### 🟢 W4 · 评分报告 + 综合收尾（教学部分）

- [ ] **W4-1** — Prompt Engineering 结构化输出（三层防御：示例 + 重试 + temperature）
- [ ] **W4-2** — 评分接口改造 + 前端动态化（已部分完成，待联调验证）
- [ ] **W4-3** — 代码改错综合练习 10 道（Java/Python/Vue/SQL）
- [ ] **W4-4** — 架构默讲（5 分钟不卡壳）+ 博客指导
- [ ] **W4-5** — SQL 终测（上）10 道
- [ ] **W4-6** — SQL 终测（下）10 道 + 全链路口述
- [ ] **W4-7** — 综合闸门验收 + 周报

---

### ⬜ SQL 每日练习

- [ ] 每天 10 道，持续到 W4-6
- [ ] 薄弱点追踪：GROUP BY / LEFT JOIN + COUNT(NULL) / UNION / COALESCE
- [ ] 出题规范：基础变式 3-4 → 新/难题 2-3 → 复习旧知识 3-4
- [ ] 终测目标：20 道综合题 16 道以上

---

### ⬜ 交付物

- [ ] **架构默讲** — 不看图，讲清 7 服务通信，5 分钟不卡壳
- [ ] **演示视频** — 5 分钟，OBS 录制，展示注册→登录→创建面试→AI 对话→评分→历史查看全流程
- [ ] **技术博客** — 1 篇以上，掘金发布，主题：RAG 或 Agent 实战
- [ ] **README 最终版** — 架构图 + Swagger 截图 + 动图 + 技术栈说明 + 快速启动

---

## 四、执行顺序建议

```
Phase 1 · 收尾 + 地基（W1 剩余 + 安全 + 体验闭环）
  W1-5 → W1-6 闸门
  → JWT Filter → 路由守卫 → 输入校验
  → 注册 → 首页 → 历史 → 创建面试
  → 全局异常 → 错误处理 → 日志

Phase 2 · 工程完整性
  API 文档 → 分页 → 前端环境变量 → 健康检查 → 版本锁定 → 数据库迁移

Phase 3 · 文件上传 + AI 深度（W2-W3 教学）
  新建上传接口 → 解析 PDF/Word → RAG 全链路 → LangGraph Agent

Phase 4 · 收尾（W4 教学）
  评分报告 → 代码改错 → 架构默讲 → SQL 终测

Phase 5 · 交付
  README 最终版 → 演示视频 → 博客
```

---

## 五、关键约束

- **用户称呼**：丢丢
- **上课触发**：说"上课" = 执行 SESSION-START.md 防线零 → SQL 30min → 五步法教学
- **SQL 铁规**：每天 10 道，不标难度，一题一题出，先看实际数据库表结构再出题
- **五步法**：讲概念 → 演示代码 → 用户讲给我听（不可跳）→ 用户口述我打字 → 我出题用户判断
- **用户信号**：喊"锚"=重新讲锚点 / 喊"慢"=放慢速度 / 喊"闭嘴"=立刻闭嘴 / 喊"存"=更新 memory
- **结课**：用户先总结 → 更新 learning-progress.md → Git commit + push
- **严禁**：跳过 Step 3、先写好代码让用户贴、不问用户就自己写新功能、为追进度加速
