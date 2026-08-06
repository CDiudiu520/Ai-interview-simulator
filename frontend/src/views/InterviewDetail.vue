<template>
  <div class="detail-page">
    <!-- 顶部 -->
    <div class="top-bar">
      <div class="top-left">
        <button class="back-btn" @click="router.back()">← 返回</button>
        <h2 class="page-title">面试详情</h2>
      </div>
      <el-button type="primary" @click="handleRetry">
        <IconKeyboardArrowReturn style="width:16px;height:16px;margin-right:4px;" />
        重新模拟
      </el-button>
    </div>

    <!-- 加载 / 错误 / 内容 -->
    <div v-if="loading" class="empty-state"><p>加载中...</p></div>
    <div v-else-if="error" class="empty-state">
      <p>{{ error }}</p>
      <el-button type="primary" @click="fetchDetail">重新加载</el-button>
    </div>

    <template v-else-if="interview">
      <!-- 基本信息 -->
      <div class="info-card">
        <div class="info-company">{{ interview.company }}</div>
        <div class="info-position">{{ interview.position }}</div>
        <div class="info-meta">
          <span class="meta-tag">{{ typeMap[interview.type] || '技术面' }}</span>
          <span class="meta-date">{{ formatDate(interview.createdAt) }}</span>
        </div>
      </div>

      <!-- 分数 -->
      <div class="score-card">
        <div class="score-big" :class="scoreClass(interview.score)">{{ interview.score ?? '—' }}<span class="score-unit">分</span></div>
        <div class="score-label">AI 综合评分</div>
      </div>

      <!-- 总评 -->
      <div v-if="interview.feedback" class="section-card">
        <h3 class="section-title">总评</h3>
        <p class="section-text">{{ interview.feedback }}</p>
      </div>

      <!-- 亮点 -->
      <div v-if="highlights.length" class="section-card">
        <h3 class="section-title">做得好的地方</h3>
        <ul class="list">
          <li v-for="(h, i) in highlights" :key="i" class="list-item good">{{ h }}</li>
        </ul>
      </div>

      <!-- 短板 -->
      <div v-if="weaknesses.length" class="section-card">
        <h3 class="section-title">需要改进</h3>
        <ul class="list">
          <li v-for="(w, i) in weaknesses" :key="i" class="list-item bad">{{ w }}</li>
        </ul>
      </div>

      <!-- 建议 -->
      <div v-if="suggestions.length" class="section-card">
        <h3 class="section-title">改进建议</h3>
        <ul class="list">
          <li v-for="(s, i) in suggestions" :key="i" class="list-item">{{ s }}</li>
        </ul>
      </div>

      <!-- 没评分 -->
      <div v-if="!interview.feedback && !highlights.length" class="empty-state">
        <p>这场面试还没有评分（可能中途退出了），无法查看报告</p>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { IconKeyboardArrowReturn } from '@iconify-prerendered/vue-streamline-freehand'

const route = useRoute()
const router = useRouter()
const typeMap = { tech: '技术面', hr: 'HR面', mixed: '综合面' }

const loading = ref(true)
const error = ref('')
const interview = ref(null)
const highlights = ref([])
const weaknesses = ref([])
const suggestions = ref([])

const formatDate = (d) => (d ? String(d).substring(0, 10) : '')
const scoreClass = (s) => {
  if (s == null) return 's-none'
  if (s >= 80) return 's-high'
  if (s >= 60) return 's-mid'
  return 's-low'
}
const parseList = (s) => {
  if (!s) return []
  try { return JSON.parse(s) } catch (e) { return [] }
}

const fetchDetail = async () => {
  loading.value = true
  error.value = ''
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/interviews/${route.params.id}`, {
      headers: { 'Authorization': `Bearer ${token}` },
    })
    if (res.ok) {
      const data = await res.json()
      interview.value = data
      highlights.value = parseList(data.highlights)
      weaknesses.value = parseList(data.weaknesses)
      suggestions.value = parseList(data.suggestions)
    } else {
      const err = await res.json().catch(() => ({}))
      error.value = err.error || '加载失败'
    }
  } catch (e) { error.value = '网络请求失败，请检查后端是否启动' }
  finally { loading.value = false }
}

const handleRetry = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/interviews`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
      body: JSON.stringify({ company: interview.value.company, position: interview.value.position, type: interview.value.type || 'tech' }),
    })
    if (!res.ok) { const err = await res.json(); ElMessage.error(err.error || '创建失败'); return }
    const created = await res.json()
    router.push({ path: `/interview/${created.id}`, query: { company: created.company, position: created.position, type: created.type || 'tech' } })
  } catch (e) { ElMessage.error('网络请求失败') }
}

onMounted(fetchDetail)
</script>

<style scoped>
.detail-page { max-width: 680px; }

.top-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.top-left { display: flex; align-items: center; gap: 14px; }
.back-btn { background: transparent; border: none; color: var(--text-secondary); font-size: 14px; cursor: pointer; padding: 4px 0; }
.back-btn:hover { color: var(--primary); }
.page-title { font-size: 22px; font-weight: 600; color: var(--text); margin: 0; }

.info-card { background: var(--surface); border: 1px solid var(--border); border-radius: 12px; padding: 24px; margin-bottom: 16px; }
.info-company { font-size: 20px; font-weight: 600; color: var(--text); margin-bottom: 4px; }
.info-position { font-size: 14px; color: var(--text-secondary); margin-bottom: 12px; }
.info-meta { display: flex; align-items: center; gap: 10px; }
.meta-tag { padding: 2px 10px; border-radius: 8px; background: var(--primary-ghost); color: var(--primary); font-weight: 500; font-size: 12px; }
.meta-date { font-size: 13px; color: var(--text-muted); }

.score-card { text-align: center; padding: 28px 0 24px; margin-bottom: 16px; background: var(--surface); border: 1px solid var(--border); border-radius: 12px; }
.score-big { font-size: 52px; font-weight: 700; }
.score-big.s-high { color: var(--success); }
.score-big.s-mid { color: var(--warning); }
.score-big.s-low { color: var(--danger); }
.score-big.s-none { color: var(--text-muted); }
.score-unit { font-size: 18px; color: var(--text-secondary); margin-left: 4px; }
.score-label { font-size: 13px; color: var(--text-secondary); margin-top: 4px; }

.section-card { background: var(--surface); border: 1px solid var(--border); border-radius: 12px; padding: 20px 24px; margin-bottom: 16px; }
.section-title { font-size: 15px; font-weight: 600; color: var(--text); margin: 0 0 12px; }
.section-text { font-size: 14px; line-height: 1.8; color: var(--text); margin: 0; white-space: pre-wrap; }
.list { margin: 0; padding-left: 18px; }
.list-item { font-size: 14px; line-height: 1.8; color: var(--text); margin-bottom: 4px; }
.list-item.good::marker { color: var(--success); }
.list-item.bad::marker { color: var(--danger); }

.empty-state { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 16px; padding: 60px 0; color: var(--text-secondary); font-size: 15px; }
</style>
