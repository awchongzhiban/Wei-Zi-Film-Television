// 导入 router/index.js
import router from './index.js'
// 导入 stores/menu.js
import { adminMenuStore } from '../stores/menu.js'
// 引入Layout
import Layout from '@/Layout/index.vue'

// 设置路由白名单
const whiteRouter = ['/','/admin-login','/error','/404']
let isFirst = true;

// 全局前置路由守卫
router.beforeEach((to, from, next) => {
    // 使用pinia
    const menuStore = adminMenuStore();
    // 白名单直接放行
    if (whiteRouter.includes(to.path)) {
        return next();
    }

    // 动态路由数据不存在，生成并添加
    if (menuStore.routerList.length === 0) {
        // 设置动态路由数据结构，并且添加到路由中
        menuStore.generateRouter().then(() => {
            // 添加动态路由
            const routerList = menuStore.routerList;
            // 所有的页面都是加载到Layout/Main组件的RouterView中
            // 相当于所有的路由都是Layout的子路由
            router.addRoute(
                {
                    component: Layout,
                    path: "/admin-index",
                    redirect: 'admin-index',
                    children: routerList
                }
            )
            isFirst = false;
            // 跳转页面
            next({...to, replace: true})
        })
    } else {
        // 动态路由数据已存在，判断是不是第一次进入改网站，防止刷新后不存在的路由，并且需要重新生成不然页面不存在的
        menuStore.setRouterList(menuStore.menuList);
        if (isFirst) {
            router.addRoute({
                component: Layout,
                path: "/admin-index",
                redirect: 'admin-index',
                children: menuStore.routerList
            });
            isFirst = false;
            // 跳转页面
            next({...to, replace: true})
        } else {
            // 路由匹配判断
            if (to.matched.length === 0) {
                // 如果没有匹配的路由，重定向到404
                next('/404');
            } else {
                // 匹配到了路由，正常导航
                next();
            }
        }
    }
    const currentMenu = menuList.value.find(menu => menu.path === to.path);
    if (currentMenu) {
        menuStore.setCurrentRoute(currentMenu);
    }
})

// 全局后置路由守卫，确保跳转成功后更新面包屑
router.afterEach((to, from) => {
    const menuStore = adminMenuStore();
    updateBreadcrumb(menuStore, to);
});

function updateBreadcrumb(menuStore, to) {
    const parentId = to.meta.parentId;
    const parentMenu = findParentById(menuStore.menuList, parentId);
    menuStore.setCurrentRoute(to.path);
    const breadcrumbList = [];
    if (parentMenu) {
        breadcrumbList.push(parentMenu.menuName); // 添加父级菜单名称到面包屑列表中
    }
    breadcrumbList.push(to.meta.title); // 添加当前菜单名称到面包屑列表中

    // 更新面包屑
    menuStore.updateBreadcrumbList(breadcrumbList);
}

function findParentById(menuList, parentId) {
    // 查找具有指定 parentId 的菜单项
    const parentMenu = menuList.find(menu => menu.menuId === parentId);
    if (!parentMenu) {
        return null; // 如果未找到对应的菜单项，则返回 null
    }
    if (parentMenu.parentId === 0) {
        return parentMenu; // 如果找到的菜单项的 parentId 为 0，则说明是父级菜单，直接返回
    }
    // 否则，递归查找父级菜单的父级菜单
    return findParentById(menuList, parentMenu.parentId);
}

