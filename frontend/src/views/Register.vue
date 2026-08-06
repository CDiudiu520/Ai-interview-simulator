<template>
  <div class="register-page">
    <div class="register-form">
      <div class="form-header">
        <IconConversationChat class="header-icon" />
        <h1 class="header-title">创建账号</h1>
        <div class="header-line"></div>
        <p class="header-sub">加入 AI 面试模拟器，开始你的面试之旅</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" class="underline-input" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱（选填）" size="large" class="underline-input" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="至少 6 位密码" size="large"
            show-password class="underline-input" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" size="large"
            show-password class="underline-input" @keyup.enter="handleRegister" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="register-btn" @click="handleRegister" :loading="loading">
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="form-footer">
        已有账号？<el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
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

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPassword = (_rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码不能少于 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' },
  ],
}

const handleRegister = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await fetch(`${import.meta.env.VITE_API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: form.username,
          password: form.password,
          email: form.email || null,
        }),
      })
      const data = await res.json()
      if (!res.ok) {
        ElMessage.error(data.error || '注册失败')
      } else {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      }
    } catch (e) {
      ElMessage.error('网络请求失败，请检查后端是否启动')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-page {
  width: 100%; min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  background: var(--bg);
}
.register-form { width: 380px; }
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
.register-btn { width: 100%; height: 46px; font-size: 15px; letter-spacing: 0.12em; border-radius: 6px; }
.form-footer { text-align: center; font-size: 14px; color: var(--text-secondary); margin-top: 8px; }
</style>
