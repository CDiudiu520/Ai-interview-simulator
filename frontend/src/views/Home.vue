<template>
  <div class="home">
    <!-- 问候 -->
    <div class="greeting">
      <h2 class="greeting-text">{{ greeting }}，{{ username }} <span class="wave">👋</span></h2>
      <p class="greeting-sub">每一次模拟面试，都是在为真正的机会做准备</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row" v-if="!loading">
      <div class="stat-card">
        <div class="stat-num">{{ stats.total }}</div>
        <div class="stat-label">模拟面试</div>
      </div>
      <div class="stat-card">
        <div class="stat-num">{{ stats.avgScore }}</div>
        <div class="stat-label">平均分</div>
      </div>
    </div>

    <!-- 模块入口卡片 -->
    <div class="hub-cards">
      <router-link to="/interview/create" class="hub-card">
        <div class="hub-icon-box">
          <IconAddSignBold class="hub-icon" />
        </div>
        <div class="hub-body">
          <div class="hub-title">开始模拟面试</div>
          <div class="hub-desc">输入 JD + 简历，AI 自动出题并进行多轮追问</div>
        </div>
        <span class="hub-arrow">→</span>
      </router-link>

      <router-link to="/interview/history" class="hub-card">
        <div class="hub-icon-box">
          <IconAlertAlarmClock class="hub-icon" />
        </div>
        <div class="hub-body">
          <div class="hub-title">面试记录</div>
          <div class="hub-desc">回顾过往面试，查看 AI 评估报告和改进建议</div>
        </div>
        <span class="hub-arrow">→</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  IconAddSignBold,
  IconAlertAlarmClock,
  IconAnalyticsBoardGraphLine,
} from '@iconify-prerendered/vue-streamline-freehand'

const username = ref('')
const stats = ref({ total: 0, avgScore: 0, latestCompany: '' })
const loading = ref(true)

const hour = new Date().getHours()
const greeting = hour < 12 ? '上午好' : hour < 18 ? '下午好' : '晚上好'

onMounted(async () => {
  username.value = localStorage.getItem('username') || '用户'
  try {
    const token = localStorage.getItem('token')
    const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/interviews/stats`, {
      headers: { 'Authorization': `Bearer ${token}` },
    })
    if (res.ok) stats.value = await res.json()
  } catch (e) {}
  loading.value = false
})
</script>

<style scoped>
.home {
  max-width: 640px;
  margin: 0 auto;
  padding-top: 24px;
}

/* ── 问候 ── */
.greeting { margin-bottom: 28px; }
.greeting-text {
  font-size: 24px;
  font-weight: 600;
  color: var(--text);
  margin: 0 0 8px;
}
.wave { font-size: 22px; }
.greeting-sub {
  font-size: 15px;
  color: var(--text-secondary);
  margin: 0;
}

/* ── 统计卡片 ── */
.stats-row { display: flex; gap: 16px; margin-bottom: 32px; }
.stat-card {
  flex: 1; padding: 20px 24px; background: var(--surface);
  border: 1px solid var(--border); border-radius: var(--radius-md); text-align: center;
}
.stat-num { font-size: 28px; font-weight: 700; color: var(--primary); margin-bottom: 4px; }
.stat-label { font-size: 13px; color: var(--text-secondary); }

/* ── 模块卡片 ── */
.hub-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.hub-card {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 24px 28px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  text-decoration: none;
  cursor: pointer;
  transition: box-shadow var(--fast) var(--ease-out), border-color var(--fast);
}
.hub-card:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-md);
}
.hub-card.locked {
  cursor: default;
  opacity: 0.55;
}
.hub-card.locked:hover {
  border-color: var(--border);
  box-shadow: none;
}

.hub-icon-box {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: var(--primary-ghost);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.locked-box { background: var(--border-light); }
.hub-icon {
  width: 24px;
  height: 24px;
  color: var(--primary);
}
.locked-icon { color: var(--text-muted); }

.hub-body { flex: 1; min-width: 0; }
.hub-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}
.hub-card.locked .hub-title { color: var(--text-secondary); }
.hub-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.5;
}

.hub-arrow { font-size: 20px; color: var(--text-muted); flex-shrink: 0; }
.hub-lock { font-size: 16px; flex-shrink: 0; }
</style>
