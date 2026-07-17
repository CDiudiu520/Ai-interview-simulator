<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: card.bg }">
              <el-icon :size="28" color="#fff"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ card.value }}</p>
              <p class="stat-label">{{ card.label }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" style="margin-top: 24px;">
      <el-col :span="14">
        <el-card>
          <template #header>
            <span class="card-title">最近面试记录</span>
          </template>
          <el-table :data="recentInterviews" style="width: 100%" stripe>
            <el-table-column prop="company" label="公司" />
            <el-table-column prop="position" label="岗位" />
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="score" label="得分" width="80">
              <template #default="{ row }">
                <el-tag :type="row.score >= 70 ? 'success' : row.score >= 50 ? 'warning' : 'danger'">
                  {{ row.score }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div style="text-align: center; margin-top: 16px;">
            <el-button type="primary" @click="$router.push('/interview/create')">
              开始新面试
            </el-button>
          </div>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card>
          <template #header>
            <span class="card-title">AI 面试技巧</span>
          </template>
          <ul class="tips-list">
            <li>📝 上传完整的 JD，AI 才能生成精准的面试题</li>
            <li>🎯 面试前先回顾你的简历亮点，AI 会重点考察</li>
            <li>💡 每次面试后查看 AI 反馈报告，针对性改进</li>
            <li>📚 上传面经文档到知识库，AI 出题更有针对性</li>
            <li>🔄 多轮追问功能可以模拟真实面试的压力感</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const statCards = ref([
  { label: '总面试次数', value: 12, icon: 'ChatLineSquare', bg: '#409EFF' },
  { label: '平均得分', value: '72.5', icon: 'TrendCharts', bg: '#67C23A' },
  { label: '今日练习', value: 3, icon: 'Star', bg: '#E6A23C' },
  
])

const recentInterviews = ref([
  { company: '字节跳动', position: '后端开发实习生', date: '2026-07-12', score: 78 },
  { company: '腾讯', position: 'AI应用实习生', date: '2026-07-10', score: 99 },
  { company: '美团', position: 'AI 应用开发', date: '2026-07-08', score: 82 },
])
</script>

<style scoped>
.stat-card {
  cursor: pointer;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-top: 4px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.tips-list {
  list-style: none;
  padding: 0;
}

.tips-list li {
  padding: 10px 0;
  color: #606266;
  line-height: 1.6;
  border-bottom: 1px solid #f0f0f0;
}

.tips-list li:last-child {
  border-bottom: none;
}
</style>
