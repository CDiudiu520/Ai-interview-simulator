<template>
  <div class="login-page">
    <div class="login-form">
      <div class="form-header">
        <IconConversationChat class="header-icon" />
        <h1 class="header-title">AI 面试模拟器</h1>
        <div class="header-line"></div>
        <p class="header-sub">打开麦克风，和 AI 面试官聊一聊</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" class="underline-input" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large"
            show-password class="underline-input" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <label class="remember-row">
            <el-checkbox v-model="form.remember" />
            <span>记住密码</span>
          </label>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" @click="handleLogin" :loading="loading">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="form-footer">
        还没有账号？<el-link type="primary" @click="handleRegister">立即注册</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { IconConversationChat } from '@iconify-prerendered/vue-streamline-freehand'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '你还没告诉我你是谁呢', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await fetch('http://127.0.0.1:8080/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: form.username,
          password: form.password
        })
      })
      const data = await res.json()
      if (data.error) {
        ElMessage.error(data.error)
      } else {
        localStorage.setItem('token', data.token)
        localStorage.setItem('username', data.username)
        ElMessage.success('登录成功')
        router.push('/home')
      }
    } catch (e) {
      ElMessage.error('网络请求失败，请检查后端是否启动')
    } finally {
      loading.value = false
    }
  })
}
const handleRegister = () => ElMessage.info('注册功能将在后续实现')
</script>

<style scoped>
.login-page {
  width: 100%; min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  background: var(--bg);
}
.login-form { width: 380px; }
.form-header { text-align: center; margin-bottom: 40px; }
.header-icon { width: 48px; height: 48px; color: var(--text); margin-bottom: 16px; }
.header-title { font-family: var(--font-display); font-size: 26px; font-weight: 400; color: var(--text); letter-spacing: 0.08em; margin: 0; }
.header-line { width: 40px; height: 2px; background: var(--primary); margin: 14px auto; border-radius: 1px; }
.header-sub { font-size: 14px; color: var(--text-secondary); margin: 0; }

:deep(.underline-input .el-input__wrapper) {
  border: none; border-bottom: 1.5px solid var(--border); border-radius: 0;
  box-shadow: none !important; background: transparent; padding: 4px 0;
}
:deep(.underline-input.is-focus .el-input__wrapper) {
  border-bottom-color: var(--primary);
}
.remember-row { display: flex; align-items: center; gap: 6px; font-size: 14px; color: var(--text-secondary); cursor: pointer; }
.login-btn { width: 100%; height: 46px; font-size: 15px; letter-spacing: 0.12em; border-radius: 6px; }
.form-footer { text-align: center; font-size: 14px; color: var(--text-secondary); margin-top: 8px; }
</style>
