<template>
  <div class="session-page">
    <!-- 面试信息栏 -->
    <el-card class="session-header">
      <div class="header-info">
        <div>
          <h3>{{ interview.company }} — {{ interview.position }}</h3>
          <p class="meta">
            <el-tag size="small">{{ typeMap[interview.type] }}</el-tag>
            <span style="margin-left: 12px;">第 {{ currentIndex + 1 }} / {{ questions.length }} 题</span>
          </p>
        </div>
        <el-button type="danger" plain @click="handleEnd">结束面试</el-button>
      </div>
    </el-card>

    <!-- 对话区域 -->
    <div class="chat-area">
      <div class="messages" ref="messagesRef">
        <div v-for="(msg, idx) in messages" :key="idx"
          :class="['message', msg.role === 'ai' ? 'ai' : 'user']">
          <div class="avatar">
            <el-avatar :size="36" :icon="msg.role === 'ai' ? 'Cpu' : 'UserFilled'"
              :style="{ background: msg.role === 'ai' ? '#409EFF' : '#67C23A' }" />
          </div>
          <div class="bubble">
            <div class="content">{{ msg.content }}</div>
            <div class="time">{{ msg.time }}</div>
          </div>
        </div>

        <!-- AI 正在输入... -->
        <div v-if="aiThinking" class="message ai">
          <div class="avatar">
            <el-avatar :size="36" icon="Cpu" style="background: #409EFF;" />
          </div>
          <div class="bubble thinking">
            <span class="dot">●</span><span class="dot">●</span><span class="dot">●</span>
            AI 正在生成...
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-input
          v-model="userInput"
          type="textarea"
          :rows="3"
          placeholder="输入你的回答..."
          :disabled="aiThinking"
          @keyup.enter.ctrl="handleSend"
        />
        <div class="input-actions">
          <span class="tip">Ctrl + Enter 发送</span>
          <el-button type="primary" @click="handleSend" :loading="aiThinking">
            发送回答
          </el-button>
        </div>
      </div>
    </div>

    <!-- 评分面板（面试结束后显示） -->
    <el-dialog v-model="showResult" title="面试结果" width="500px">
      <div class="result" v-if="result">
        <el-result :icon="result.score >= 70 ? 'success' : 'warning'"
          :title="`综合得分：${result.score}`" />
        <el-descriptions :column="1" border>
          <el-descriptions-item label="逻辑能力">⭐⭐⭐⭐</el-descriptions-item>
          <el-descriptions-item label="专业深度">⭐⭐⭐</el-descriptions-item>
          <el-descriptions-item label="表达准确度">⭐⭐⭐⭐</el-descriptions-item>
        </el-descriptions>
        <div class="feedback">
          <h4>AI 改进建议：</h4>
          <p>{{ result.feedback }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// ---- 模拟面试数据 ----
const interview = reactive({
  company: '字节跳动',
  position: '后端开发实习生',
  type: 'tech',
})

const typeMap = { tech: '技术面', hr: 'HR面', mixed: '综合面' }

const questions = ref([
  '请介绍一下你简历中提到的最有挑战性的项目？',
  '在项目中你用了哪些技术栈？为什么选择这些技术？',
  '请讲讲你对 Java 并发编程的理解？',
  '如果要你设计一个高并发系统，你会从哪些方面考虑？',
  '你对我们公司的业务有什么了解？为什么想加入我们？',
])

const currentIndex = ref(0)
const userInput = ref('')
const aiThinking = ref(false)
const showResult = ref(false)
const messagesRef = ref(null)
const result = ref(null)

const messages = ref([
  {
    role: 'ai',
    content: '欢迎参加本次模拟面试！我是你的 AI 面试官。我们先从第一个问题开始：' + questions.value[0],
    time: formatTime(),
  },
])

function formatTime() {
  return new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const handleSend = async () => {
  const text = userInput.value.trim()
  if (!text || aiThinking.value) return

  // 添加用户消息
  messages.value.push({ role: 'user', content: text, time: formatTime() })
  userInput.value = ''
  await scrollToBottom()

  // 模拟 AI 思考
  aiThinking.value = true
  await new Promise(r => setTimeout(r, 1500 + Math.random() * 1000))
  aiThinking.value = false

  // 下一题或结束
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++
    messages.value.push({
      role: 'ai',
      content: '好的，我们来看下一题：' + questions.value[currentIndex.value],
      time: formatTime(),
    })
  } else {
    // 面试结束
    messages.value.push({
      role: 'ai',
      content: '面试结束！我已经根据你的表现生成了评估报告，请看右侧弹窗。',
      time: formatTime(),
    })
    result.value = {
      score: Math.floor(Math.random() * 30) + 60,
      feedback: '你的项目经验描述清晰，对基础技术原理的理解较为扎实。建议在系统设计方面多练习高并发场景的分析思路，同时在表达上可以更结构化（用 STAR 法则：情境→任务→行动→结果）。继续加油！',
    }
    showResult.value = true
  }

  await scrollToBottom()
}

const handleEnd = () => {
  ElMessageBox.confirm('确定要结束当前面试吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    ElMessage.info('面试已结束')
    showResult.value = true
  }).catch(() => {})
}

const scrollToBottom = async () => {
  await nextTick()
  const el = messagesRef.value
  if (el) el.scrollTop = el.scrollHeight
}
</script>

<style scoped>
.session-page {
  max-width: 900px;
  margin: 0 auto;
}

.session-header {
  margin-bottom: 16px;
}

.header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info h3 {
  margin: 0 0 6px;
}

.meta {
  color: #909399;
  font-size: 13px;
}

.chat-area {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e6e6e6;
  overflow: hidden;
}

.messages {
  height: 420px;
  overflow-y: auto;
  padding: 20px;
  background: #fafafa;
}

.message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.message.user {
  flex-direction: row-reverse;
}

.bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  position: relative;
}

.message.ai .bubble {
  background: #fff;
  border: 1px solid #e6e6e6;
}

.message.user .bubble {
  background: #409EFF;
  color: #fff;
}

.bubble .content {
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.bubble .time {
  font-size: 11px;
  color: #999;
  margin-top: 6px;
  text-align: right;
}

.message.user .bubble .time {
  color: rgba(255,255,255,0.7);
}

.thinking {
  color: #909399;
}

.dot {
  animation: blink 1.4s infinite both;
  margin-right: 3px;
}

.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes blink {
  0% { opacity: 0.2; }
  20% { opacity: 1; }
  100% { opacity: 0.2; }
}

.input-area {
  padding: 16px 20px;
  border-top: 1px solid #e6e6e6;
  background: #fff;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.tip {
  color: #c0c4cc;
  font-size: 12px;
}

.feedback {
  margin-top: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  line-height: 1.8;
  color: #606266;
}

.feedback h4 {
  margin-bottom: 8px;
  color: #303133;
}
</style>
