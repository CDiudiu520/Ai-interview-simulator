<template>
  <div class="home">
    <!-- 问候 -->
    <div class="greeting">
      <h2 class="greeting-text">下午好，管理员 <span class="wave">👋</span></h2>
      <p class="greeting-sub">每一次模拟面试，都是在为真正的机会做准备</p>
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

      <div class="hub-card locked">
        <div class="hub-icon-box locked-box">
          <IconAnalyticsBoardGraphLine class="hub-icon locked-icon" />
        </div>
        <div class="hub-body">
          <div class="hub-title">面试统计</div>
          <div class="hub-desc">能力雷达图 + 得分趋势 + 弱项分析</div>
        </div>
        <span class="hub-lock">🔒</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  IconAddSignBold,
  IconAlertAlarmClock,
  IconAnalyticsBoardGraphLine,
} from '@iconify-prerendered/vue-streamline-freehand'
</script>

<style scoped>
.home {
  max-width: 640px;
  margin: 0 auto;
  padding-top: 24px;
}

/* ── 问候 ── */
.greeting { margin-bottom: 40px; }
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
