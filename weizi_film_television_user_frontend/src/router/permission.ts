// 导入 router/index.ts
import router from './index.ts'
import { NavigationGuardNext, RouteLocationNormalized, RouteLocationNormalizedLoaded } from 'vue-router';
import { useScrollAndLoadStore } from "../stores/scrollAndLoadStroe.ts"

// 全局前置路由守卫
router.beforeEach((to: RouteLocationNormalized, from: RouteLocationNormalizedLoaded, next: NavigationGuardNext) => {
    // 每次跳转路由都先重置数据
    useScrollAndLoadStore().resetScrollAndLoadStore();
    return next();
})

