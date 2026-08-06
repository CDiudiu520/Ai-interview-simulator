"""
RAG 简化实现：分块 → 词袋向量 → 余弦相似度检索

背景：DeepSeek 不提供 embedding API，这里用最朴素但够用的方案——
      中文按"字+词"切分建成词袋向量，用余弦相似度检索。
      数据量小（几份文档），面试出题场景够用。
      接口设计成 add_document()/search()，将来换真 embedding 或 Chroma 只改这两个函数。

教学点：
  1. 分块：文档长不能全塞，按段落/句切，控制每块大小
  2. 向量化：把文本变成数字数组（词袋）
  3. 检索：query 也转向量，余弦相似度排序取前 K
"""

import re
import numpy as np

# ---------- 1. 分块 ----------

def chunk_text(text: str, max_len: int = 500) -> list[str]:
    """
    把长文本切成块。

    切分规则（段落是天然边界，绝不跨段落合并）：
      1. 按段落（一个以上换行）粗分，每段独立成块 —— 保证语义完整
      2. 单段超过 max_len 的，按句子（。！？；）细切成多块
      3. 单句仍超长的，硬切兜底
    """
    if not text or not text.strip():
        return []

    # 第一步：按段落切（支持单/多换行分隔）
    paragraphs = re.split(r"\n+", text)
    paragraphs = [p.strip() for p in paragraphs if p.strip()]

    # 第二步：段落超长的按句子细切
    chunks: list[str] = []
    for para in paragraphs:
        if len(para) <= max_len:
            chunks.append(para)
        else:
            chunks.extend(_split_long_paragraph(para, max_len))
    return chunks


def _split_long_paragraph(para: str, max_len: int) -> list[str]:
    """超长段落按中文句号/感叹号/问号/分号切开"""
    sentences = re.split(r"(?<=[。！？；])", para)
    sentences = [s.strip() for s in sentences if s.strip()]

    # 如果句子本身还超长，硬切（兜底）
    out: list[str] = []
    current = ""
    for s in sentences:
        if len(s) > max_len:  # 单句超长，硬切
            if current:
                out.append(current)
                current = ""
            for i in range(0, len(s), max_len):
                out.append(s[i:i + max_len])
        elif len(current) + len(s) <= max_len:
            current += s
        else:
            out.append(current)
            current = s
    if current:
        out.append(current)
    return out


# ---------- 2. 向量化（词袋） ----------

# 停用词：无实义、出现频率高但对检索没帮助的词
STOP_WORDS = set("的了是我你在有他这那和就都而对与或及把被让给从到去又也一个不啊嗯哦好吧呢吗吧啥".strip())

# 词库：所有见过的词/字（动态扩展）
_vocab: dict[str, int] = {}  # word -> index


def _tokenize(text: str) -> list[str]:
    """
    中文切词：按 2 字词 + 单字 混合。
    简化实现：对中文，拆成相邻 2 字组合 + 单独字符，英文数字按空白拆。
    """
    tokens: list[str] = []
    # 英文/数字词
    for en in re.findall(r"[a-zA-Z0-9_]+", text):
        tokens.append(en.lower())
    # 中文字符
    cjk = re.sub(r"[a-zA-Z0-9_]+", " ", text)
    cjk_chars = [c for c in cjk if "一" <= c <= "鿿"]
    # 2 字词（窗口滑动）
    for i in range(len(cjk_chars) - 1):
        tokens.append(cjk_chars[i] + cjk_chars[i + 1])
    # 单字（补充，用于短文档命中）
    for c in cjk_chars:
        tokens.append(c)
    # 过滤停用词和过短词
    return [t for t in tokens if t not in STOP_WORDS and len(t) > 1]


def _text_to_vec(text: str, expand_vocab: bool = True) -> np.ndarray:
    """
    文本 → 词袋向量。
    expand_vocab=True 时允许动态加词（建索引用）；
    expand_vocab=False 时只按当前词库计算，不扩展（检索 query 用，
    否则 query 里的新词会改变向量维度，和已存向量对不上）。
    """
    global _vocab
    tokens = _tokenize(text)
    if expand_vocab:
        for token in tokens:
            if token not in _vocab:
                _vocab[token] = len(_vocab)
    vec = np.zeros(len(_vocab), dtype=np.float32)
    for token in tokens:
        idx = _vocab.get(token)
        if idx is not None:
            vec[idx] += 1
    return vec


# ---------- 3. 文档存储与检索 ----------

class RAGIndex:
    """文档索引：存每个块的向量 + 原文，支持按 query 检索"""

    def __init__(self):
        self.chunks: list[str] = []
        self.chunk_vecs: list[np.ndarray] = []
        self._pending: list[str] = []  # 待建索引的块（等词库定稿后再向量化）

    def add_document(self, text: str) -> int:
        """把一份文档分块，先暂存。返回块数量。"""
        chunks = chunk_text(text)
        self._pending.extend(chunks)
        return len(chunks)

    def build_index(self) -> int:
        """
        对全部待建块统一向量化（两阶段：先收集词库，再统一生成向量）。
        返回本次实际构建的块数量。之后新增文档需再调一次。
        """
        if not self._pending:
            return 0
        # 阶段一：先把所有块的新词加入词库（向量维度定稿）
        for c in self._pending:
            _text_to_vec(c, expand_vocab=True)
        # 阶段二：词库不再变化，统一按定稿维度生成向量
        for c in self._pending:
            self.chunks.append(c)
            self.chunk_vecs.append(_text_to_vec(c, expand_vocab=False))
        count = len(self._pending)
        self._pending = []
        return count

    def search(self, query: str, k: int = 3) -> list[str]:
        """查 query 最相关的 k 个块，返回原文列表"""
        if not self.chunks:
            return []
        qvec = _text_to_vec(query, expand_vocab=False)
        # 余弦相似度：cos = (q·v) / (|q| * |v|)
        scores = []
        for v in self.chunk_vecs:
            qn = np.linalg.norm(qvec)
            vn = np.linalg.norm(v)
            if qn == 0 or vn == 0:
                scores.append(0.0)
            else:
                scores.append(float(np.dot(qvec, v) / (qn * vn)))
        # 取前 k 个，返回原文
        top_idx = np.argsort(scores)[::-1][:k]
        return [self.chunks[i] for i in top_idx if scores[i] > 0]


# 全局单例：进程内存中保存，重启即清空（教学够用；生产应换向量库）
index = RAGIndex()
