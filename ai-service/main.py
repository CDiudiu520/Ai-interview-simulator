from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import os
import sys
import json
import io
from pathlib import Path
from dotenv import load_dotenv
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage
from langchain_core.chat_history import InMemoryChatMessageHistory

# 把当前目录加入 Python 搜索路径
sys.path.insert(0, str(Path(__file__).parent))

load_dotenv(Path(__file__).parent / ".env")
from db import fetch_all
from rag import index  # RAG 检索索引（进程内存，重启清空）

app = FastAPI()
store = {}  # key: session_id, value: InMemoryChatMessageHistory
documents = {}  # key: document_id, value: 文档元信息（标题等）

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


@app.post("/upload-document")
async def upload_document(file: UploadFile = File(...)):
    """接收 PDF/Word 文件，解析并返回文本内容"""
    # 检查文件类型
    filename = file.filename or ""
    ext = filename.rsplit(".", 1)[-1].lower() if "." in filename else ""

    if ext not in ("pdf", "docx", "doc", "txt", "md"):
        return {"error": f"不支持的文件格式: .{ext}，支持 PDF/Word/TXT/Markdown"}

    try:
        content = await file.read()

        if ext == "pdf":
            # PDF 解析
            from pypdf import PdfReader
            reader = PdfReader(io.BytesIO(content))
            text_parts = []
            for page in reader.pages:
                page_text = page.extract_text()
                if page_text:
                    text_parts.append(page_text)
            text = "\n\n".join(text_parts)

        elif ext in ("docx", "doc"):
            # Word 解析
            from docx import Document
            doc = Document(io.BytesIO(content))
            text_parts = []
            for para in doc.paragraphs:
                if para.text.strip():
                    text_parts.append(para.text)
            text = "\n".join(text_parts)

        else:
            # TXT/MD 直接读
            text = content.decode("utf-8")

        if not text.strip():
            return {"error": "文件内容为空，无法提取文本"}

        # 加入 RAG 索引：分块 + 向量化，之后出题可引用
        doc_id = str(uuid.uuid4())
        documents[doc_id] = {"filename": filename, "length": len(text)}
        chunk_count = index.add_document(text)
        index.build_index()

        return {
            "filename": filename,
            "text": text,
            "length": len(text),
            "document_id": doc_id,
            "chunk_count": chunk_count,
        }

    except Exception as e:
        return {"error": f"文件解析失败: {str(e)}"}


class JDRequest(BaseModel):
    jd_text: str
    count: int = 5
    document_id: str | None = None  # 可选：上传文档的ID，出题引用文档内容


@app.post("/generate-questions")
def generate_questions(req: JDRequest):
    api_key = os.getenv("DEEPSEEK_API_KEY")

    # 1. 创建遥控器
    llm = ChatOpenAI(
        base_url="https://api.deepseek.com/v1",
        api_key=api_key,
        model="deepseek-v4-pro",
        temperature=0.7
    )

    # 2. 拼消息：system + 用户输入的JD
    system_prompt = (
        f"你是一个专业的面试官。请根据职位描述生成{req.count}道面试题，每题标注考察点。"
        "不要用Markdown格式，用纯文本：每道题用编号'第X题：'开头，题目和考察点之间用换行隔开。"
    )

    # 2.5 如果上传了文档，检索相关片段拼进 Prompt（RAG）
    human_content = req.jd_text
    if req.document_id:
        retrieved = index.search(req.jd_text, k=3)
        if retrieved:
            doc_snippet = "\n\n".join(retrieved)
            system_prompt += (
                "\n\n下面是候选人上传的面经/简历中与本次面试相关的片段，"
                "出题时必须参考这些内容，优先考察文档里提到的知识点。"
                f"\n【文档片段】\n{doc_snippet}"
            )

    messages = [
        SystemMessage(content=system_prompt),
        HumanMessage(content=human_content)
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
    system_prompt = (
        "你是一个专业的面试官。用户消息中会包含面试进度信息（第X/Y题、追问轮数、题目列表）。"
        "规则如下：\n"
        "1. 每道题追问2-3轮，深入考察候选人的理解深度\n"
        "2. 追问够了之后，在回复末尾加上【下一题】，然后简短过渡到下一题\n"
        "3. 如果是最后一题且追问够了，在回复末尾加上【面试结束】，并给一句结束语\n"
        "4. 不要一次问多个问题，保持一对一对话节奏\n"
        "5. 不要在回复中重复显示用户发来的进度信息"
    )
    messages = [SystemMessage(content=system_prompt)]
    messages += history.messages

    # 4. 调 LLM
    llm = ChatOpenAI(
        base_url="https://api.deepseek.com/v1",
        api_key=api_key,
        model="deepseek-v4-pro",
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
        model="deepseek-v4-pro",
        temperature=0.3      # 打分需要更稳定，temperature 低一点
    )

    # 2. 拼消息：system + 转换角色
    messages = [SystemMessage(content="你是一个专业的面试评估官。请根据对话历史对候选人打分（0-100分），并给出以下内容：\n- feedback：2-3 句话的总评\n- highlights：1-3 个具体亮点（用数组，每个是一句话）\n- weaknesses：最关键的 1-3 个短板（挑最重要的，不是全部列出来。用数组，每个是一句话，引用对话中的具体例子）\n- suggestions：2-3 条可操作的改进建议（用数组，每个是一句话）\n\n只返回JSON格式，不要返回其他内容：\n{\"score\": 82, \"feedback\": \"总评...\", \"highlights\": [\"亮点1\", \"亮点2\"], \"weaknesses\": [\"短板1\"], \"suggestions\": [\"建议1\", \"建议2\"]}")]
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
        return {
            "score": result["score"],
            "feedback": result["feedback"],
            "highlights": result.get("highlights", []),
            "weaknesses": result.get("weaknesses", []),
            "suggestions": result.get("suggestions", [])
        }

    except Exception as e:
        return {"error": str(e)}
