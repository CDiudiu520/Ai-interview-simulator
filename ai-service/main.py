from fastapi import FastAPI
from pydantic import BaseModel
import requests
import os
import sys
from pathlib import Path
from dotenv import load_dotenv

# 把当前目录加入 Python 搜索路径
sys.path.insert(0, str(Path(__file__).parent))

load_dotenv(Path(__file__).parent / ".env")
from db import fetch_all

app = FastAPI()


@app.get("/")
def hello():
    return {"message": "AI Interview Simulator 后端启动成功！"}


@app.get("/ping")
def ping():
    return "pong"


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

    try:
        response = requests.post(
            "https://api.deepseek.com/v1/chat/completions",
            headers={
                "Authorization": f"Bearer {api_key}",
                "Content-Type": "application/json"
            },
            json={
                "model": "deepseek-chat",
                "messages": [
                    {"role": "system", "content": "你是一个专业的面试官。请根据职位描述生成5道面试题，每题标注考察点。"},
                    {"role": "user", "content": req.jd_text}
                ]
            },
            timeout=60
        )
        data = response.json()

        if "choices" not in data:
            return {"error": "DeepSeek 返回异常", "detail": data}

        questions_text = data["choices"][0]["message"]["content"]
        return {"questions": questions_text}

    except requests.Timeout:
        return {"error": "请求超时，DeepSeek 没有在 60 秒内响应"}
    except Exception as e:
        return {"error": str(e)}
