<template>
  <div class="session-page">
    <!-- 信息栏 -->
    <div class="session-top">
      <div class="top-left">
        <h2 class="top-company">{{ interview.company }} — {{ interview.position }}</h2>
        <div class="top-meta">
          <span class="meta-type">{{ typeMap[interview.type] }}</span>
          <span class="meta-sep">·</span>
          <span class="meta-progress">第 {{ currentIndex + 1 }} / {{ interview.count }} 题</span>
        </div>
      </div>
      <button class="btn-end" @click="handleEnd">结束面试</button>
    </div>

    <!-- 对话面板 -->
    <div class="chat-panel">
      <div class="chat-messages" ref="messagesRef">
        <div v-for="(msg, idx) in messages" :key="idx" :class="['chat-msg', msg.role]">
          <div class="msg-avatar">
            <div class="avatar-dot" :class="msg.role">
              <IconMicroprocessorComputerChipProcessor v-if="msg.role === 'ai'" class="dot-icon" />
              <IconAppWindowUser v-else class="dot-icon" />
            </div>
          </div>
          <div class="msg-body">
            <div class="msg-content">{{ msg.content }}</div>
            <div class="msg-time">{{ msg.time }}</div>
          </div>
        </div>

        <div v-if="aiThinking" class="chat-msg ai">
          <div class="msg-avatar">
            <div class="avatar-dot ai"><IconMicroprocessorComputerChipProcessor class="dot-icon" /></div>
          </div>
          <div class="msg-body">
            <div class="msg-thinking">
              <span class="dot">●</span><span class="dot">●</span><span class="dot">●</span> AI 正在思考...
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input v-model="userInput" type="textarea" :rows="3" placeholder="输入你的回答..." :disabled="aiThinking"
          class="msg-textarea" @keyup.enter.ctrl="handleSend" />
        <div class="input-bar">
          <span class="input-hint">Ctrl + Enter 发送</span>
          <el-button type="primary" @click="handleSend" :loading="aiThinking" size="large">发送回答</el-button>
        </div>
      </div>
    </div>

    <!-- 结果弹窗 -->
    <el-dialog v-model="showResult" title="面试结果" width="480px">
      <div class="result-panel" v-if="result">
        <div class="result-score"><span class="score-big">{{ result.score }}</span><span class="score-suffix">分</span></div>
        <div class="result-dims">
          <div class="dim-row"><span class="dim-label">逻辑能力</span><span class="dim-stars">⭐⭐⭐⭐</span></div>
          <div class="dim-row"><span class="dim-label">专业深度</span><span class="dim-stars">⭐⭐⭐</span></div>
          <div class="dim-row"><span class="dim-label">表达准确度</span><span class="dim-stars">⭐⭐⭐⭐</span></div>
        </div>
        <div class="result-feedback">
          <div class="feedback-title">AI 改进建议</div>
          <p>{{ result.feedback }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IconMicroprocessorComputerChipProcessor, IconAppWindowUser } from '@iconify-prerendered/vue-streamline-freehand'

const router = useRouter()
const route = useRoute()

const interview = reactive({
  company: route.query.company || '未指定公司', position: route.query.position || '未指定岗位',
  type: route.query.type || 'tech', jd: route.query.jd || '', count: Number(route.query.count) || 5,
})
const typeMap = { tech: '技术面', hr: 'HR面', mixed: '综合面' }
const questions = ref([])
const loadingQuestions = ref(true)
const currentIndex = ref(0)
const userInput = ref('')
const aiThinking = ref(false)
const showResult = ref(false)
const messagesRef = ref(null)
const result = ref(null)
const messages = ref([{ role: 'ai', content: 'AI 面试官正在准备面试题，请稍候...', time: formatTime() }])

function formatTime() { return new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) }

onMounted(async () => {
  try {
    const jd = interview.jd || `${interview.company} ${interview.position}`
    const res = await fetch('http://127.0.0.1:8080/ai/generate-questions', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ jd_text: jd, count: interview.count || 5 })
    })
    const data = await res.json()
    if (data.questions) {
      const parts = data.questions.split(/第\d+题：/).filter(p => p.trim())
      questions.value = parts.map(p => p.replace(/\n/g, ' ').trim()).filter(p => p.length > 5)
      if (questions.value.length > 0) {
        messages.value[0].content = '欢迎参加本次模拟面试！我是你的 AI 面试官。我们先从第一个问题开始：' + questions.value[0]
      }
    }
  } catch (e) { console.error(e); ElMessage.error('题目生成失败，请返回重试') }
  loadingQuestions.value = false
})

const sessionId = ref(null)
const handleSend = async () => {
  const text = userInput.value.trim()
  if (!text || aiThinking.value) return
  messages.value.push({ role: 'user', content: text, time: formatTime() })
  userInput.value = ''; await scrollToBottom()
  aiThinking.value = true
  try {
    const res = await fetch('http://127.0.0.1:8080/ai/chat', {
      method: 'POST', headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ session_id: sessionId.value, message: text })
    })
    const data = await res.json()
    if (data.reply) { messages.value.push({ role: 'ai', content: data.reply, time: formatTime() }); sessionId.value = data.session_id }
  } catch (e) { console.error(e) }
  aiThinking.value = false; await scrollToBottom()
}

const handleEnd = () => {
  ElMessageBox.confirm('确定要结束当前面试吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    .then(async () => {
      ElMessage.info('正在 AI 评估中...')
      try {
        const history = messages.value.map(m => ({ role: m.role, content: m.content }))
        const res = await fetch('http://127.0.0.1:8080/ai/evaluate', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ messages: history }) })
        const data = await res.json()
        if (data.score !== undefined) { result.value = { score: data.score, feedback: data.feedback } }
      } catch (e) { console.error(e) }
      showResult.value = true
    }).catch(() => {})
}

const scrollToBottom = async () => { await nextTick(); const el = messagesRef.value; if (el) el.scrollTop = el.scrollHeight }
</script>

<style scoped>
.session-page { height: calc(100vh - 56px - 60px); display: flex; flex-direction: column; max-width: 800px; }

.session-top { display: flex; align-items: center; justify-content: space-between; padding-bottom: 16px; border-bottom: 1px solid var(--border); margin-bottom: 16px; flex-shrink: 0; }
.top-company { font-size: 16px; font-weight: 600; color: var(--text); margin: 0 0 4px; }
.top-meta { display: flex; align-items: center; gap: 8px; font-size: 13px; }
.meta-type { padding: 2px 10px; border-radius: 8px; background: var(--primary-ghost); color: var(--primary); font-weight: 500; font-size: 12px; }
.meta-sep { color: var(--text-muted); }
.meta-progress { color: var(--text-secondary); }
.btn-end { padding: 8px 18px; border: 1px solid var(--danger); background: transparent; color: var(--danger); border-radius: 6px; font-size: 13px; cursor: pointer; }
.btn-end:hover { background: #FDF0F0; }

.chat-panel { flex: 1; display: flex; flex-direction: column; background: var(--surface); border: 1px solid var(--border); border-radius: 12px; overflow: hidden; min-height: 0; }
.chat-messages { flex: 1; overflow-y: auto; padding: 24px; background: var(--bg); min-height: 0; }
.chat-msg { display: flex; gap: 12px; margin-bottom: 24px; }
.chat-msg.user { flex-direction: row-reverse; }
.msg-avatar { flex-shrink: 0; padding-top: 2px; }
.avatar-dot { width: 34px; height: 34px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.avatar-dot.ai { background: var(--text); }
.avatar-dot.user { background: var(--primary); }
.dot-icon { width: 16px; height: 16px; color: #fff; }
.msg-body { max-width: 70%; }
.msg-content { padding: 14px 18px; border-radius: 12px; font-size: 14px; line-height: 1.75; white-space: pre-wrap; word-break: break-word; }
.chat-msg.ai .msg-content { background: var(--surface); border: 1px solid var(--border); border-top-left-radius: 4px; }
.chat-msg.user .msg-content { background: var(--text); color: #fff; border-top-right-radius: 4px; }
.msg-time { font-size: 11px; margin-top: 4px; text-align: right; padding: 0 4px; color: var(--text-muted); }

.msg-thinking { padding: 14px 18px; border-radius: 12px; border-top-left-radius: 4px; background: var(--surface); border: 1px solid var(--border); font-size: 14px; color: var(--text-secondary); }
.dot { animation: blink 1.4s infinite both; margin-right: 2px; }
.dot:nth-child(2) { animation-delay: 0.2s; } .dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes blink { 0% { opacity: 0.2; } 20% { opacity: 1; } 100% { opacity: 0.2; } }

.chat-input { padding: 16px 24px; border-top: 1px solid var(--border); background: var(--surface); }
:deep(.msg-textarea .el-textarea__inner) { border: 1px solid var(--border); border-radius: 8px; background: var(--bg); resize: none; font-size: 14px; line-height: 1.7; }
:deep(.msg-textarea .el-textarea__inner:focus) { border-color: var(--primary); box-shadow: none; }
.input-bar { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; }
.input-hint { font-size: 12px; color: var(--text-muted); }

.result-panel { text-align: center; }
.result-score { margin: 16px 0; }
.score-big { font-size: 48px; font-weight: 700; color: var(--primary); }
.score-suffix { font-size: 18px; color: var(--text-secondary); margin-left: 4px; }
.result-dims { margin: 20px 0; text-align: left; }
.dim-row { display: flex; justify-content: space-between; align-items: center; padding: 10px 0; border-bottom: 1px solid var(--border-light); }
.dim-row:last-child { border-bottom: none; }
.dim-label { font-size: 14px; color: var(--text); }
.result-feedback { text-align: left; margin-top: 16px; padding: 16px; background: var(--bg); border-radius: 8px; border: 1px solid var(--border); line-height: 1.8; font-size: 14px; }
.feedback-title { font-size: 13px; font-weight: 600; color: var(--text-secondary); margin-bottom: 8px; text-transform: uppercase; letter-spacing: 0.05em; }
</style>
