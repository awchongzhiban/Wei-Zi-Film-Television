// 导入 router/index.js
import router from './index.js'
// 导入 stores/menu.js
import { useMenuStore } from '../stores/menu.js'
// 引入Layout
import Layout from '@/Layout/index.vue'

// 设置路由白名单
const whiteRouter = ['/','/admin-login','/error','/404']

// 全局前置路由守卫
router.beforeEach((to, from, next) => {
    // 使用pinia
    const menuStore = useMenuStore();
    // 判断 to 是否处于白名单
    if (whiteRouter.indexOf(to.path) === -1) {
        // 判断routerList中是否有动态路由的数据
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
                // 跳转页面
                next({...to, replace: true})
            })
        } else {
            // 已经有动态路由了
            // 情况1：路由的路径是合法的，正常的
            if (to.matched.length !== 0) {
                next();
            } else {
                // 情况2：路由的页面并没有
                next('/404');
            }
        }
    } else {
        // 直接放行
        next();
    }
})

// 全局后置路由守卫，确保跳转成功后更新面包屑
router.afterEach((to, from) => {
    const menuStore = useMenuStore();
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

