<template>
  <div class="create-page">
    <el-card>
      <template #header>
        <span class="card-title">创建新面试</span>
      </template>

      <el-form :model="form" label-width="100px" style="max-width: 700px;">
        <el-form-item label="目标公司">
          <el-input v-model="form.company" placeholder="例如：字节跳动" />
        </el-form-item>

        <el-form-item label="目标岗位">
          <el-input v-model="form.position" placeholder="例如：后端开发实习生" />
        </el-form-item>

        <el-form-item label="岗位 JD">
          <el-input v-model="form.jd" type="textarea" :rows="6"
            placeholder="粘贴目标岗位的 JD（职位描述），AI 会根据 JD 生成针对性面试题..." />
        </el-form-item>

        <el-form-item label="你的简历">
          <div class="upload-area">
            <el-upload
              drag
              :auto-upload="false"
              :on-change="handleResumeChange"
              :limit="1"
              accept=".pdf,.txt,.md,.doc,.docx"
            >
              <el-icon class="el-icon--upload" :size="40"><UploadFilled /></el-icon>
              <div class="el-upload__text">
                拖拽或 <em>点击上传</em> 简历文件
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持 PDF / Word / TXT / Markdown 格式
                </div>
              </template>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="面试类型">
          <el-radio-group v-model="form.type">
            <el-radio-button value="tech">技术面</el-radio-button>
            <el-radio-button value="hr">HR 面</el-radio-button>
            <el-radio-button value="mixed">综合面</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="题目数量">
          <el-slider v-model="form.questionCount" :min="3" :max="15" show-stops
            style="width: 200px;" />
          <span style="margin-left: 16px; color: #909399;">{{ form.questionCount }} 题</span>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="handleStart" :loading="starting">
            <el-icon><VideoPlay /></el-icon>
            开始 AI 面试
          </el-button>
          <el-button size="large" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const starting = ref(false)

const form = reactive({
  company: '',
  position: '',
  jd: '',
  type: 'tech',
  questionCount: 5,
  resumeFile: null,
})

const handleResumeChange = (file) => {
  form.resumeFile = file.raw
}

const handleStart = () => {
  if (!form.company || !form.position) {
    ElMessage.warning('请至少填写目标公司和岗位')
    return
  }
  starting.value = true
  // 模拟创建面试，后续接入 AI 服务
  setTimeout(() => {
    starting.value = false
    ElMessage.success('面试已创建，AI 正在生成题目...')
    router.push('/interview/1')
  }, 1000)
}

const handleReset = () => {
  form.company = ''
  form.position = ''
  form.jd = ''
  form.type = 'tech'
  form.questionCount = 5
  form.resumeFile = null
}
</script>

<style scoped>
.card-title {
  font-size: 16px;
  font-weight: 600;
}

.upload-area {
  width: 100%;
}

.el-upload__tip {
  margin-top: 8px;
}
</style>
