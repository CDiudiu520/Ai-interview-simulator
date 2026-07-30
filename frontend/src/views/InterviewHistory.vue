<template>
  <div class="history-page">
    <!-- 顶部栏 -->
    <div class="top-bar">
      <div class="top-left">
        <h2 class="page-title">面试记录</h2>
        <span class="page-count">{{ allInterviews.length }} 场模拟面试</span>
      </div>
      <div class="top-right">
        <el-input v-model="search" placeholder="搜索公司或岗位..." class="search-input" clearable />
        <el-select v-model="filterType" placeholder="全部类型" class="filter-select" clearable>
          <el-option label="技术面" value="tech" />
          <el-option label="HR 面" value="hr" />
          <el-option label="综合面" value="mixed" />
        </el-select>
        <el-button type="primary" @click="$router.push('/interview/create')">
          <IconAddSignBold style="width:17px;height:17px;margin-right:4px;" />
          创建面试
        </el-button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-if="filteredList.length === 0" class="empty-state">
      <p v-if="search || filterType">没有找到匹配的面试记录</p>
      <p v-else>还没有面试记录，去创建你的第一场模拟面试吧</p>
      <el-button v-if="!search && !filterType" type="primary" @click="$router.push('/interview/create')">
        创建面试
      </el-button>
    </div>

    <!-- 时间轴 -->
    <div
      v-else
      class="timeline-stage"
      ref="stageRef"
      @wheel.prevent="onWheel"
      @pointerdown="onPointerDown"
      @pointermove="onPointerMove"
      @pointerup="onPointerUp"
      @pointercancel="onPointerUp"
      @pointerleave="onPointerUp"
    >
      <div class="timeline-track" ref="trackRef" :style="{ transform: `translate3d(${offset}px, 0, 0)` }">
        <!-- 横线 -->
        <div class="timeline-line"></div>

        <!-- 节点 + 卡片 -->
        <div
          v-for="(item, idx) in filteredList"
          :key="item.id"
          class="timeline-node"
          :style="{ left: idx * cardGap + 'px' }"
          @click="goDetail(item.id)"
        >
          <!-- 竖线 -->
          <div class="node-stem"></div>
          <!-- 圆点 -->
          <div class="node-dot" :class="scoreClass(item.score)"></div>
          <!-- 卡片 -->
          <div class="node-card">
            <div class="card-company">{{ item.company }}</div>
            <div class="card-position">{{ item.position }}</div>
            <div class="card-meta">
              <span class="card-type">{{ typeMap[item.type] }}</span>
              <span class="card-score" :class="scoreClass(item.score)">{{ item.score }}分</span>
            </div>
            <div class="card-date">{{ item.date }}</div>
          </div>
        </div>

        <!-- 终点箭头 -->
        <div class="timeline-end" :style="{ left: filteredList.length * cardGap + 'px' }">→</div>
      </div>

      <!-- 拖拽提示 -->
      <div class="drag-hint">← 拖动浏览 →</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { IconAddSignBold } from '@iconify-prerendered/vue-streamline-freehand'

const router = useRouter()

const search = ref('')
const filterType = ref('')
const typeMap = { tech: '技术面', hr: 'HR面', mixed: '综合面' }

const allInterviews = ref([
  { id: 1, company: '字节跳动', position: '后端开发实习生', type: 'tech', date: '07-12', score: 78, duration: '25分钟' },
  { id: 2, company: '阿里巴巴', position: 'Java 开发实习', type: 'tech', date: '07-10', score: 65, duration: '30分钟' },
  { id: 3, company: '美团', position: 'AI 应用开发', type: 'mixed', date: '07-08', score: 82, duration: '20分钟' },
  { id: 4, company: '腾讯', position: '前端开发实习', type: 'tech', date: '07-05', score: 71, duration: '28分钟' },
  { id: 5, company: '华为', position: '云计算实习生', type: 'tech', date: '07-03', score: 55, duration: '22分钟' },
  { id: 6, company: '小红书', position: '产品经理实习', type: 'hr', date: '07-01', score: 88, duration: '18分钟' },
  { id: 7, company: '百度', position: 'AI 产品实习', type: 'mixed', date: '06-28', score: 73, duration: '26分钟' },
  { id: 8, company: '网易', position: '游戏策划实习', type: 'hr', date: '06-25', score: 60, duration: '15分钟' },
])

const filteredList = computed(() => {
  let list = allInterviews.value
  if (search.value) {
    const s = search.value.toLowerCase()
    list = list.filter(item => item.company.toLowerCase().includes(s) || item.position.toLowerCase().includes(s))
  }
  if (filterType.value) {
    list = list.filter(item => item.type === filterType.value)
  }
  return list
})

const scoreClass = (score) => {
  if (score >= 80) return 's-high'
  if (score >= 60) return 's-mid'
  return 's-low'
}

const goDetail = (id) => {
  if (Math.abs(dragDelta) > 4) return
  router.push(`/interview/${id}`)
}

/* ── 拖拽 / 滚轮 ── */
const stageRef = ref(null)
const trackRef = ref(null)
const cardGap = 220
const offset = ref(0)
let minOffset = 0
let dragging = false
let dragOrigin = { x: 0, offset: 0 }
let dragDelta = 0

const clampOffset = () => {
  if (!stageRef.value || !trackRef.value) return
  const stageW = stageRef.value.clientWidth
  const trackW = filteredList.value.length * cardGap + 80
  minOffset = Math.min(0, stageW - trackW - 40)
  offset.value = Math.max(minOffset, Math.min(40, offset.value))
}

const onWheel = (e) => {
  offset.value -= e.deltaY * 1.2
  clampOffset()
}

const onPointerDown = (e) => {
  dragging = true
  dragDelta = 0
  dragOrigin = { x: e.clientX, offset: offset.value }
  stageRef.value?.setPointerCapture(e.pointerId)
}

const onPointerMove = (e) => {
  if (!dragging) return
  const dx = e.clientX - dragOrigin.x
  dragDelta = Math.abs(dx)
  offset.value = dragOrigin.offset + dx
  clampOffset()
}

const onPointerUp = () => {
  dragging = false
}

let resizeTimer = 0
const onResize = () => {
  clearTimeout(resizeTimer)
  resizeTimer = setTimeout(clampOffset, 100)
}

onMounted(() => {
  clampOffset()
  window.addEventListener('resize', onResize)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
})
</script>

<style scoped>
.history-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 56px - 60px);
}

/* ── 顶部栏 ── */
.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}
.top-left { display: flex; align-items: baseline; gap: 14px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--text); margin: 0; }
.page-count { font-size: 14px; color: var(--text-secondary); }
.top-right { display: flex; align-items: center; gap: 10px; }
.search-input { width: 220px; }
.filter-select { width: 130px; }

/* ── 空状态 ── */
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  color: var(--text-secondary);
  font-size: 15px;
}

/* ═══════════════════════════════════════════
   时间轴
   ═══════════════════════════════════════════ */
.timeline-stage {
  flex: 1;
  position: relative;
  overflow: hidden;
  cursor: grab;
  user-select: none;
  touch-action: none;
}
.timeline-stage:active { cursor: grabbing; }

.timeline-track {
  position: relative;
  height: 100%;
  padding: 60px 60px 40px;
  min-width: max-content;
  transition: none;
  will-change: transform;
}

/* 横线 */
.timeline-line {
  position: absolute;
  top: 76px;
  left: 60px;
  right: 60px;
  height: 2px;
  background: var(--border);
  border-radius: 1px;
}

/* 终点箭头 */
.timeline-end {
  position: absolute;
  top: 64px;
  font-size: 24px;
  color: var(--text-muted);
  transform: translateX(20px);
}

/* 每个节点 */
.timeline-node {
  position: absolute;
  top: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.node-stem {
  width: 2px;
  height: 20px;
  background: var(--border);
  margin-bottom: 0;
}
.node-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  border: 3px solid var(--surface);
  box-shadow: 0 0 0 2px var(--border);
  flex-shrink: 0;
  position: relative;
  z-index: 2;
  transition: transform var(--fast);
}
.timeline-node:hover .node-dot { transform: scale(1.3); }
.node-dot.s-high { background: var(--success); box-shadow: 0 0 0 2px var(--success); }
.node-dot.s-mid { background: var(--warning); box-shadow: 0 0 0 2px var(--warning); }
.node-dot.s-low { background: var(--danger); box-shadow: 0 0 0 2px var(--danger); }

/* 卡片 */
.node-card {
  margin-top: 14px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 16px 18px;
  width: 180px;
  text-align: center;
  transition: border-color var(--fast), box-shadow var(--fast);
}
.timeline-node:hover .node-card {
  border-color: var(--primary);
  box-shadow: var(--shadow-md);
}
.card-company { font-size: 15px; font-weight: 600; color: var(--text); margin-bottom: 2px; }
.card-position { font-size: 13px; color: var(--text-secondary); margin-bottom: 10px; }
.card-meta { display: flex; justify-content: center; align-items: center; gap: 10px; margin-bottom: 8px; }
.card-type {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 6px;
  background: var(--primary-ghost);
  color: var(--primary);
  font-weight: 500;
}
.card-score { font-size: 13px; font-weight: 600; }
.card-score.s-high { color: var(--success); }
.card-score.s-mid { color: var(--warning); }
.card-score.s-low { color: var(--danger); }
.card-date { font-size: 12px; color: var(--text-muted); }

/* 拖拽提示 */
.drag-hint {
  position: absolute;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  color: var(--text-muted);
  pointer-events: none;
}
</style>
