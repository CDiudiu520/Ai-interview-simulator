from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import os
import sys
import json
from pathlib import Path
from dotenv import load_dotenv
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage
from langchain_core.chat_history import InMemoryChatMessageHistory

# 把当前目录加入 Python 搜索路径
sys.path.insert(0, str(Path(__file__).parent))

load_dotenv(Path(__file__).parent / ".env")
from db import fetch_all

app = FastAPI()
store = {}  # key: session_id, value: InMemoryChatMessageHistory

# 允许前端跨域访问
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],      # 允许所有来源
    allow_methods=["*"],      # 允许所有方法
    allow_headers=["*"],
)


@app.get("/")
def hello():
    return {"message": "AI Interview Simulator 后端启动成功！"}


@app.get("/ping")
def ping():
    return "pong"


@app.get("/status")
def get_status():
    user_rows = fetch_all("SELECT COUNT(*) AS count FROM users")
    user_count = user_rows[0]['count']
    interview_rows = fetch_all("SELECT COUNT(*) AS count FROM interviews")
    interview_count = interview_rows[0]['count']
    return {"用户数": user_count, "面试数": interview_count}


@app.get("/interviews")
def get_interviews():
    """从 MySQL 读取所有面试记录，连带用户名"""
    sql = """
        SELECT i.id, u.username, i.company, i.position, i.score, i.created_at
        FROM interviews i
        JOIN users u ON u.id = i.user_id
        ORDER BY i.created_at DESC
    """
    rows = fetch_all(sql)
    return {"count": len(rows), "data": rows}


class JDRequest(BaseModel):
    jd_text: str


@app.post("/generate-questions")
def generate_questions(req: JDRequest):
    api_key = os.getenv("DEEPSEEK_API_KEY")

    # 1. 创建遥控器
    llm = ChatOpenAI(
        base_url="https://api.deepseek.com/v1",
        api_key=api_key,
        model="deepseek-chat",
        temperature=0.7
    )

    # 2. 拼消息：system + 用户输入的JD
    messages = [
        SystemMessage(content="你是一个专业的面试官。请根据职位描述生成5道面试题，每题标注考察点。不要用Markdown格式，用纯文本：每道题用编号'第X题：'开头，题目和考察点之间用换行隔开。"),
        HumanMessage(content=req.jd_text)
    ]

    try:
        # 3. 调 LLM
        response = llm.invoke(messages)
        # 4. 返回回复
        return {"questions": response.content}

    except Exception as e:
        return {"error": str(e)}


import uuid

class ChatRequest(BaseModel):
    session_id: str | None = None  # 会话ID，首次为空
    message: str                    # 用户刚发送的一句话

@app.post("/chat")
def chat(req: ChatRequest):
    api_key = os.getenv("DEEPSEEK_API_KEY")

    # 1. 找旧对话 or 创建新对话
    sid = req.session_id
    if not sid or sid not in store:
        sid = str(uuid.uuid4())
        store[sid] = InMemoryChatMessageHistory()

    history = store[sid]

    # 2. 把用户消息加入历史
    history.add_user_message(req.message)

    # 3. 拼完整消息：system + 全部历史
    messages = [SystemMessage(content="你是一个专业的面试官。请根据候选人的回答进行追问或点评。如果候选人回答得好，深入追问细节；如果回答不好，引导候选人思考。保持对话节奏，不要一次问太多问题。")]
    messages += history.messages

    # 4. 调 LLM
    llm = ChatOpenAI(
        base_url="https://api.deepseek.com/v1",
        api_key=api_key,
        model="deepseek-chat",
        temperature=0.7
    )

    try:
        response = llm.invoke(messages)
        # 5. AI 回复也存进历史
        history.add_ai_message(response.content)
        return {"reply": response.content, "session_id": sid}

    except Exception as e:
        return {"error": str(e)}


class EvalRequest(BaseModel):
    messages: list[dict]

@app.post("/evaluate")
def evaluate(req: EvalRequest):
    api_key = os.getenv("DEEPSEEK_API_KEY")

    # 1. 创建遥控器
    llm = ChatOpenAI(
        base_url="https://api.deepseek.com/v1",
        api_key=api_key,
        model="deepseek-chat",
        temperature=0.3      # 打分需要更稳定，temperature 低一点
    )

    # 2. 拼消息：system + 转换角色
    messages = [SystemMessage(content="你是一个专业的面试评估官。请根据对话历史对候选人打分（0-100分）并给出评语。只返回JSON格式：{\"score\": 数字, \"feedback\": \"评语\"}，不要返回其他内容。")]
    for m in req.messages:
        if m["role"] == "user":
            messages.append(HumanMessage(content=m["content"]))
        elif m["role"] == "ai":
            messages.append(AIMessage(content=m["content"]))

    # 3. 调 LLM
    try:
        response = llm.invoke(messages)
        # 4. 解析 JSON 结果
        result = json.loads(response.content)
        return {"score": result["score"], "feedback": result["feedback"]}

    except Exception as e:
        return {"error": str(e)}
