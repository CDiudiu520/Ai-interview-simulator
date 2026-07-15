<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <el-icon :size="32" color="#409EFF"><ChatDotRound /></el-icon>
          <h2>AI 面试模拟器</h2>
          <p>登录你的账号开始模拟面试</p>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large"
            show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" @click="handleLogin" :loading="loading">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-tip">
        还没有账号？<el-link type="primary" @click="handleRegister">立即注册</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '你还没告诉我你是谁呢', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = () => {
  formRef.value?.validate((valid) => {
    if (!valid) return
    loading.value = true
    // 模拟登录，后续接入真实 API
    setTimeout(() => {
      loading.value = false
      ElMessage.success('登录成功')
      router.push('/home')
    }, 800)
  })
}

const handleRegister = () => {
  ElMessage.info('注册功能将在后续实现')
}
</script>

<style scoped>
.login-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 12px 0 4px;
  color: #303133;
}

.card-header p {
  color: #909399;
  font-size: 14px;
}

.register-tip {
  text-align: center;
  color: #909399;
  font-size: 14px;
}
</style>
