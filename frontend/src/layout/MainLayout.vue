<template>
  <el-container class="layout">
    <!-- 侧边栏 -->
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon :size="28"><ChatDotRound /></el-icon>
        <span>AI 面试模拟器</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>工作台</span>
        </el-menu-item>
        <el-menu-item index="/interview/create">
          <el-icon><Plus /></el-icon>
          <span>创建面试</span>
        </el-menu-item>
        <el-menu-item index="/interview/history">
          <el-icon><Timer /></el-icon>
          <span>面试记录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容 -->
    <el-container>
      <el-header class="header">
        <span class="title">{{ $route.meta.title || '' }}</span>
        <el-dropdown>
          <span class="user">
            <el-avatar :size="32" icon="UserFilled" />
            <span style="margin-left: 8px;">管理员</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/login')">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)
</script>

<style scoped>
.layout {
  height: 100vh;
}

.aside {
  background-color: #304156;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 24px;
}

.title {
  font-size: 18px;
  font-weight: 500;
  color: #303133;
}

.user {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.el-main {
  background: #f5f7fa;
  padding: 24px;
}
</style>
