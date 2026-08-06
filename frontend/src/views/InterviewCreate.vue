<template>
  <div class="create-page">
    <div class="page-header">
      <h2 class="page-title">创建新面试</h2>
      <p class="page-desc">填写信息，AI 为你生成针对性面试题</p>
    </div>

    <el-form :model="form" label-width="100px" class="create-form">
      <el-form-item label="目标公司">
        <el-input v-model="form.company" placeholder="例如：字节跳动" class="underline-input" />
      </el-form-item>
      <el-form-item label="目标岗位">
        <el-input v-model="form.position" placeholder="例如：后端开发实习生" class="underline-input" />
      </el-form-item>
      <el-form-item label="岗位 JD">
        <el-input v-model="form.jd" type="textarea" :rows="5" placeholder="粘贴岗位 JD，AI 据此生成针对性面试题..." />
      </el-form-item>
      <el-form-item label="你的简历">
        <el-upload drag :auto-upload="false" :on-change="handleResumeChange" :limit="1" accept=".pdf,.txt,.md,.doc,.docx" class="resume-upload">
          <IconDrawerDownload style="width:36px;height:36px;color:var(--text-muted);margin-bottom:8px;" />
          <div class="upload-text">拖拽或 <em>点击上传</em> 简历文件</div>
          <template #tip><div class="upload-tip">{{ uploading ? '正在解析文件...' : 'PDF / Word / TXT / Markdown，解析后自动填入 JD' }}</div></template>
        </el-upload>
      </el-form-item>
      <el-form-item label="面试类型">
        <div class="type-options">
          <label v-for="opt in typeOptions" :key="opt.value" class="type-opt" :class="{ active: form.type === opt.value }">
            <input type="radio" :value="opt.value" v-model="form.type" />
            <span class="type-dot"></span>
            <span>{{ opt.label }}</span>
          </label>
        </div>
      </el-form-item>
      <el-form-item label="题目数量">
        <div class="count-options">
          <button v-for="n in countPresets" :key="n" type="button" class="count-btn" :class="{ active: form.questionCount === n && !customCount }" @click="selectPreset(n)">{{ n }} 题</button>
          <button type="button" class="count-btn" :class="{ active: customCount }" @click="customCount = true">自定义</button>
          <input v-if="customCount" v-model.number="form.questionCount" type="number" class="count-input" min="1" max="30" @blur="clampCustom" />
        </div>
      </el-form-item>
      <div class="form-actions">
        <el-button type="primary" size="large" @click="handleStart" :loading="starting">
          <IconEqualizerStereoPlay style="width:18px;height:18px;margin-right:5px;" />开始 AI 面试
        </el-button>
        <el-button size="large" @click="handleReset">重置</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { IconDrawerDownload, IconEqualizerStereoPlay } from '@iconify-prerendered/vue-streamline-freehand'

const router = useRouter()
const starting = ref(false)
const customCount = ref(false)

const typeOptions = [
  { label: '技术面', value: 'tech' },
  { label: 'HR 面', value: 'hr' },
  { label: '综合面', value: 'mixed' },
]
const countPresets = [3, 5, 8, 10, 15]

const selectPreset = (n) => { customCount.value = false; form.questionCount = n }
const clampCustom = () => {
  if (!form.questionCount || form.questionCount < 1) form.questionCount = 5
  if (form.questionCount > 30) form.questionCount = 30
}

const uploading = ref(false)
const form = reactive({ company: '', position: '', jd: '', type: 'tech', questionCount: 5, resumeFile: null })
const handleResumeChange = async (file) => {
  form.resumeFile = file.raw
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    const res = await fetch('http://127.0.0.1:8000/upload-document', { method: 'POST', body: formData })
    const data = await res.json()
    if (data.error) { ElMessage.error(data.error) }
    else {
      form.jd = form.jd + (form.jd ? '\n\n--- 简历内容 ---\n' : '') + data.text
      ElMessage.success(`已解析 ${file.raw.name}，共 ${data.length} 字符`)
    }
  } catch (e) { ElMessage.error('文件上传失败，请确认 AI 服务已启动') }
  finally { uploading.value = false }
}
const handleStart = async () => {
  if (!form.company || !form.position) { ElMessage.warning('请至少填写目标公司和岗位'); return }
  starting.value = true
  try {
    const token = localStorage.getItem('token')
    const headers = { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` }
    const createRes = await fetch(`${import.meta.env.VITE_API_BASE_URL}/interviews`, {
      method: 'POST', headers, body: JSON.stringify({ company: form.company, position: form.position, type: form.type }),
    })
    if (!createRes.ok) { const err = await createRes.json(); ElMessage.error(err.error || '创建面试失败'); return }
    const interview = await createRes.json()
    const aiRes = await fetch(`${import.meta.env.VITE_API_BASE_URL}/ai/generate-questions`, {
      method: 'POST', headers,
      body: JSON.stringify({ jd_text: form.jd || `${form.company} ${form.position} 岗位面试`, count: form.questionCount }),
    })
    const aiData = await aiRes.json()
    ElMessage.success('面试已创建，AI 正在生成题目...')
    router.push({ path: `/interview/${interview.id}`, query: { company: form.company, position: form.position, jd: form.jd, type: form.type, count: form.questionCount, questions: aiData.questions } })
  } catch (e) { ElMessage.error('网络请求失败，请检查后端是否启动') }
  finally { starting.value = false }
}
const handleReset = () => { form.company = ''; form.position = ''; form.jd = ''; form.type = 'tech'; form.questionCount = 5; form.resumeFile = null }
</script>

<style scoped>
.create-page { max-width: 660px; }
.page-header { margin-bottom: 36px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--text); margin: 0 0 6px; }
.page-desc { font-size: 14px; color: var(--text-secondary); margin: 0; }

:deep(.underline-input .el-input__wrapper) {
  border: none; border-bottom: 1.5px solid var(--border); border-radius: 0;
  box-shadow: none !important; background: transparent; padding: 4px 0;
}
:deep(.underline-input.is-focus .el-input__wrapper) { border-bottom-color: var(--primary); }

.resume-upload { width: 100%; }
:deep(.resume-upload .el-upload-dragger) { border: 1.5px dashed var(--border) !important; border-radius: 10px !important; background: transparent !important; }
:deep(.resume-upload .el-upload-dragger:hover) { border-color: var(--primary) !important; }
.upload-text { font-size: 14px; color: var(--text-secondary); }
.upload-text em { color: var(--primary); font-style: normal; }
.upload-tip { font-size: 12px; color: var(--text-muted); margin-top: 6px; }

.type-options { display: flex; gap: 8px; }
.type-opt { display: flex; align-items: center; gap: 8px; padding: 8px 18px; border: 1px solid var(--border); border-radius: 8px; cursor: pointer; font-size: 14px; color: var(--text-secondary); transition: all var(--fast); }
.type-opt input { display: none; }
.type-opt:hover { border-color: var(--text-muted); }
.type-opt.active { border-color: var(--primary); color: var(--primary); background: var(--primary-ghost); }
.type-dot { width: 8px; height: 8px; border-radius: 50%; border: 1.5px solid currentColor; }
.type-opt.active .type-dot { background: var(--primary); border-color: var(--primary); }

.count-options { display: flex; gap: 8px; align-items: center; }
.count-btn { padding: 7px 18px; border: 1px solid var(--border); border-radius: 8px; background: transparent; font-size: 14px; color: var(--text-secondary); cursor: pointer; font-family: var(--font-body); transition: all var(--fast); }
.count-btn:hover { border-color: var(--text-muted); color: var(--text); }
.count-btn.active { border-color: var(--primary); color: var(--primary); background: var(--primary-ghost); }
.count-input { width: 60px; padding: 6px 10px; border: 1px solid var(--primary); border-radius: 8px; background: transparent; font-size: 14px; color: var(--text); text-align: center; outline: none; font-family: var(--font-body); }

.form-actions { margin-top: 8px; padding-left: 100px; display: flex; gap: 12px; }
</style>
