import { createRouter, createWebHistory } from 'vue-router'
import AdminLogin from '../views/admin-login.vue'
// 配置路由规则
const constRouter = [
  // 重定向
  {
    path: '',
    name: 'DefaultAdminLogin',
    redirect: '/admin-login'
  },
  // 重定向
  {
    path: '/admin-login',
    name: 'AdminLogin',
    component: AdminLogin
  }
]

// 创建路由
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  // 使用路路由规则常量
  routes: constRouter
})

// 导出router
export default router
