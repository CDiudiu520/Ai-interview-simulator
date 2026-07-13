import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '工作台' },
      },
      {
        path: 'interview/create',
        name: 'InterviewCreate',
        component: () => import('../views/InterviewCreate.vue'),
        meta: { title: '创建面试' },
      },
      {
        path: 'interview/history',
        name: 'InterviewHistory',
        component: () => import('../views/InterviewHistory.vue'),
        meta: { title: '面试记录' },
      },
      {
        path: 'interview/:id',
        name: 'InterviewSession',
        component: () => import('../views/InterviewSession.vue'),
        meta: { title: '面试对话' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
