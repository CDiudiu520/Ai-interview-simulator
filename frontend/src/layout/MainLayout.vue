<template>
  <div class="app-shell">
    <header class="topbar">
      <div class="topbar-inner">
        <router-link to="/home" class="logo-area">
          <IconConversationChat class="logo-icon" />
          <span class="logo-text">AI 面试模拟器</span>
        </router-link>

        <nav class="nav-links">
          <router-link to="/home" class="nav-item" :class="{ active: $route.path === '/home' }">
            <IconHome class="nav-icon" />
            <span>工作台</span>
          </router-link>
          <router-link to="/interview/create" class="nav-item" :class="{ active: $route.path === '/interview/create' }">
            <IconAddSignBold class="nav-icon" />
            <span>创建面试</span>
          </router-link>
          <router-link to="/interview/history" class="nav-item" :class="{ active: $route.path === '/interview/history' }">
            <IconAlertAlarmClock class="nav-icon" />
            <span>面试记录</span>
          </router-link>
        </nav>

        <div class="user-area">
          <el-dropdown>
            <span class="user-trigger">
              <div class="avatar-circle">
                <IconAppWindowUser class="avatar-icon" />
              </div>
              <span class="user-name">{{ username || '用户' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <main class="main-area">
      <router-view />
    </main>

    <footer class="app-footer">
      <span>Icons by Streamline</span>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  IconConversationChat,
  IconHome,
  IconAddSignBold,
  IconAlertAlarmClock,
  IconAppWindowUser,
} from '@iconify-prerendered/vue-streamline-freehand'

const router = useRouter()
const username = ref('')

onMounted(() => {
  username.value = localStorage.getItem('username') || '用户'
})

const handleLogout = async () => {
  try {
    const token = localStorage.getItem('token')
    await fetch(`${import.meta.env.VITE_API_BASE_URL}/auth/logout`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
    })
  } catch (e) {}
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg);
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 50;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
}
.topbar-inner {
  max-width: 1100px;
  margin: 0 auto;
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 28px;
  gap: 32px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  flex-shrink: 0;
}
.logo-icon { width: 26px; height: 26px; color: var(--primary); }
.logo-text {
  font-family: var(--font-display);
  font-size: 18px;
  color: var(--text);
  letter-spacing: 0.05em;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  justify-content: center;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  color: var(--text-secondary);
  text-decoration: none;
  transition: all var(--fast) var(--ease-out);
}
.nav-item:hover { color: var(--text); background: rgba(0,0,0,0.03); }
.nav-item.active { color: var(--primary); background: var(--primary-ghost); }
.nav-icon { width: 18px; height: 18px; flex-shrink: 0; }

.user-area { flex-shrink: 0; }
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
}
.user-trigger:hover { background: rgba(0,0,0,0.03); }
.avatar-circle {
  width: 30px; height: 30px;
  border-radius: 50%;
  background: var(--text);
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-icon { width: 16px; height: 16px; color: #fff; }
.user-name { font-size: 14px; color: var(--text-secondary); }

.main-area {
  flex: 1;
  max-width: 1100px;
  margin: 0 auto;
  width: 100%;
  padding: 28px;
  box-sizing: border-box;
}

.app-footer {
  text-align: center;
  padding: 16px 0 24px;
  font-size: 11px;
  color: var(--text-muted);
  letter-spacing: 0.04em;
}
</style>
