<template>
  <div class="history-page">
    <el-card>
      <template #header>
        <div class="header-row">
          <span class="card-title">面试记录</span>
          <el-button type="primary" @click="$router.push('/interview/create')">
            <el-icon><Plus /></el-icon> 创建面试
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="search" placeholder="搜索公司或岗位..." style="width: 300px;" clearable />
        <el-select v-model="filterType" placeholder="面试类型" style="width: 150px; margin-left: 12px;" clearable>
          <el-option label="技术面" value="tech" />
          <el-option label="HR 面" value="hr" />
          <el-option label="综合面" value="mixed" />
        </el-select>
      </div>

      <!-- 表格 -->
      <el-table :data="filteredList" stripe style="width: 100%; margin-top: 16px;" v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="company" label="公司" width="150" />
        <el-table-column prop="position" label="岗位" width="180" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ typeMap[row.type] || row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="score" label="得分" width="100">
          <template #default="{ row }">
            <el-progress
              :percentage="row.score"
              :color="row.score >= 70 ? '#67C23A' : row.score >= 50 ? '#E6A23C' : '#F56C6C'"
              :stroke-width="16"
            />
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长" width="100" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/interview/${row.id}`)">
              查看
            </el-button>
            <el-button size="small" type="primary" link>报告</el-button>
            <el-button size="small" type="danger" link>删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div style="margin-top: 16px; text-align: right;">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="10"
          :total="total"
          layout="total, prev, pager, next"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const search = ref('')
const filterType = ref('')
const loading = ref(false)
const currentPage = ref(1)
const total = ref(8)

const typeMap = { tech: '技术面', hr: 'HR面', mixed: '综合面' }

// 模拟数据
const allInterviews = ref([
  { id: 1, company: '字节跳动', position: '后端开发实习生', type: 'tech', date: '2026-07-12', score: 78, duration: '25分钟' },
  { id: 2, company: '阿里巴巴', position: 'Java 开发实习', type: 'tech', date: '2026-07-10', score: 65, duration: '30分钟' },
  { id: 3, company: '美团', position: 'AI 应用开发', type: 'mixed', date: '2026-07-08', score: 82, duration: '20分钟' },
  { id: 4, company: '腾讯', position: '前端开发实习', type: 'tech', date: '2026-07-05', score: 71, duration: '28分钟' },
  { id: 5, company: '华为', position: '云计算实习生', type: 'tech', date: '2026-07-03', score: 55, duration: '22分钟' },
  { id: 6, company: '小红书', position: '产品经理实习', type: 'hr', date: '2026-07-01', score: 88, duration: '18分钟' },
  { id: 7, company: '百度', position: 'AI 产品实习', type: 'mixed', date: '2026-06-28', score: 73, duration: '26分钟' },
  { id: 8, company: '网易', position: '游戏策划实习', type: 'hr', date: '2026-06-25', score: 60, duration: '15分钟' },
])

const filteredList = computed(() => {
  let list = allInterviews.value
  if (search.value) {
    const s = search.value.toLowerCase()
    list = list.filter(item =>
      item.company.toLowerCase().includes(s) || item.position.toLowerCase().includes(s)
    )
  }
  if (filterType.value) {
    list = list.filter(item => item.type === filterType.value)
  }
  return list
})
</script>

<style scoped>
.card-title {
  font-size: 16px;
  font-weight: 600;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.search-bar {
  display: flex;
  align-items: center;
}
</style>
