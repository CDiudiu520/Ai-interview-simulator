"""
LangGraph Agent：把面试状态管理从"前端"移到"后端"。

背景：改造前 /chat 无状态——前端自己计数题号/追问轮数，靠 AI 回复里的
【下一题】/【面试结束】标记驱动。改造后，状态全部由后端 Agent 管理：
AI 只负责"回答当前问题"，追问/换题/结束由代码决策（有兜底，不依赖 AI 标记）。

State（每场面试一份）：
  questions         题目列表
  current_question  当前第几题（0 起）
  history           当前题的对话历史 [{role, content}]
  follow_up_count   当前题已追问几轮
  max_follow_ups    每道题最多追问轮数（代码兜底）
  done              面试是否结束
"""

import os
from typing import TypedDict
from langgraph.graph import StateGraph, START, END
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage, AIMessage


class InterviewState(TypedDict):
    questions: list[str]
    current_question: int
    history: list[dict]
    follow_up_count: int
    max_follow_ups: int
    done: bool
    # 输出（必须声明，否则 StateGraph 会丢弃未声明字段）
    reply: str  # AI 本轮回复
    next_question: bool  # 本轮该换题了
    interview_over: bool  # 面试结束


MAX_FOLLOW_UPS = 3  # 每道题最多追问 3 轮


def _llm():
    return ChatOpenAI(
        base_url="https://api.deepseek.com/v1",
        api_key=os.getenv("DEEPSEEK_API_KEY"),
        model="deepseek-v4-pro",
        temperature=0.7
    )


def _current_question_text(state: InterviewState) -> str:
    q = state["questions"][state["current_question"]]
    return f"【第{state['current_question'] + 1}题】{q}"


# ---------- 节点 1：生成 AI 回复 ----------

def generate_reply(state: InterviewState) -> dict:
    """根据当前题 + 对话历史 + 用户最新消息，生成 AI 回复。"""
    question_text = _current_question_text(state)

    system_prompt = (
        "你是一个专业的面试官，正在逐题面试候选人。\n"
        "规则：\n"
        "1. 针对当前这道题提问，一次只问一个方向，不要一次问多个问题\n"
        "2. 如果候选人答得浅，追问更深入的问题；答得好就认可并继续\n"
        "3. 每道题追问不要超过 3 轮（会有代码兜底，你正常发挥即可）\n"
        "4. 保持面试官语气，简洁专业\n"
        f"当前题目：{question_text}"
    )

    # 把当前题的历史转成 langchain 消息
    messages = [SystemMessage(content=system_prompt)]
    for m in state["history"]:
        if m["role"] == "user":
            messages.append(HumanMessage(content=m["content"]))
        elif m["role"] == "ai":
            messages.append(AIMessage(content=m["content"]))

    try:
        response = _llm().invoke(messages)
        return {"reply": response.content}
    except Exception as e:
        return {"reply": f"（AI 调用失败：{str(e)}）"}


# ---------- 节点 2：决策下一步 ----------

def decide_next(state: InterviewState) -> dict:
    """
    决定面试下一步：追问 / 换题 / 结束。
    用代码兜底，不依赖 AI 回复里的标记：
      - 当前题追问达到 max_follow_ups → 换题
      - 最后一题也追问够了 → 结束
      - 否则 → 继续追问当前题
    """
    done = state.get("done", False)
    follow_up_count = state.get("follow_up_count", 0) + 1  # 本轮算一次
    current_question = state.get("current_question", 0)
    max_follow = state.get("max_follow_ups", MAX_FOLLOW_UPS)

    # 信号每次必须显式重置（LangGraph 字段"本次没更新就保留旧值"，会粘滞）
    if follow_up_count >= max_follow:
        # 这道题追问够了，看还有没有下一题
        if current_question + 1 < len(state["questions"]):
            return {
                "current_question": current_question + 1,
                "follow_up_count": 0,
                "next_question": True,
                "interview_over": False,
                "done": False,
            }
        else:
            return {
                "next_question": False,
                "interview_over": True,
                "done": True,
            }
    else:
        return {
            "follow_up_count": follow_up_count,
            "next_question": False,
            "interview_over": False,
        }


# ---------- 编译图 ----------

def build_agent():
    graph = StateGraph(InterviewState)
    graph.add_node("generate_reply", generate_reply)
    graph.add_node("decide_next", decide_next)
    graph.add_edge(START, "generate_reply")
    graph.add_edge("generate_reply", "decide_next")
    graph.add_edge("decide_next", END)
    return graph.compile()


agent = build_agent()
