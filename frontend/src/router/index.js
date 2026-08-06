import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
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
        path: 'interview/detail/:id',
        name: 'InterviewDetail',
        component: () => import('../views/InterviewDetail.vue'),
        meta: { title: '面试详情' },
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

// 路由守卫
const WHITE_LIST = ['/login', '/register']

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (WHITE_LIST.includes(to.path)) {
    if (token) next('/home')
    else next()
  } else {
    if (!token) next('/login')
    else next()
  }
})

export default router
