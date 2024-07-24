import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router'
// 引入Layout
import Layout from '@/layout/index.vue'

const modules = import.meta.glob("../views/**.vue")
// 配置路由规则
const routes: RouteRecordRaw[] = [
  // 重定向根路径到/index
  {
    path: '',
    redirect: '/channel/choice'
  },
  // 主路由配置，使用Layout作为布局组件，并包含子路由
  {
    path: '/channel',
    component: Layout,
    children: [ // 将children改为数组形式
      {
        path: 'choice', // 子路由的path留空或使用'/'表示作为父路由的默认子路由
        name: 'Home',
        component: import('@/views/home-page.vue') // 使用动态导入语法
      }
    ]
  }
];


// 创建路由
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  // 使用路路由规则常量
  routes
})

// 导出router
export default router
