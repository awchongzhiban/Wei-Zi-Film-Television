// 引入pinia
import { defineStore } from 'pinia'
// 导入查询用户菜单接口
import { searchSelfRouter } from '@/api/admin/index.js'
// 定义 store 并导出
export const adminMenuStore = defineStore('menu', {
    // 定义状态【数据】
    state: () => ({
        menuList: [],
        routerList: [], // 动态路由数据，也就是左侧菜单的路由信息
        breadcrumbList: [],
        buttonPermissions: [], // 页面按钮权限 以router作为key
        currentRoute: '',
    }),
    // 获取数据的方法
    getters: {
        Array: (state) => state.menuList,
        Array: (state) => state.routerList,
        Array: (state) => state.breadcrumbList,
        Array: (state) => state.buttonPermissions,
        String: state => state.currentRoute,
    },

    // 编辑数据方法
    actions: {
        setMenuList(data) {
            this.menuList = data;
        },
        // 渲染动态路由数据结构，存储到pinia中，不需要每次都去渲染数据结构了
        // data就是查询出来的用户菜单
        setRouterList(data) {
            this.routerList = [];
            // 硬编码的“首页”路由信息
            const hardcodedHomeRoute = {
                name: '首页',
                path: '/admin-index',
                meta: {
                    title: '首页',
                    parentId: 0,
                },
                component: () => import(/*@vite-ignore */`../views/admin-index.vue`),
            };

            // 先将“首页”路由添加到routerList
            this.routerList.push(hardcodedHomeRoute);

            data.forEach(item => {
                // 如果 children 的 menuType 是 "BUTTON"，则不处理
                if (item.menuType === 'BUTTON' || !item.path) {
                    return;
                }
                // 定义数据结构
                let routerInfo = {
                    name: item.menuName,
                    path: item.path,
                    meta: {
                        title: item.menuName,
                        parentId: item.parentId,
                    },
                    // 设置组件，
                    component: () => import(/*@vite-ignore */`../views${item.path}.vue`)
                    // component: modules[`../views/${item.path}.vue`]
                }
                // 将路由信息添加到数组中
                this.routerList.push(routerInfo)
                // 设置子菜单
                let childrenList = item.children;
                childrenList.forEach(children => {
                    if (item.menuType === 'BUTTON' || !children.path) {
                        return;
                    }
                    let routerInfo = {
                        name: children.menuName,
                        path: children.path,
                        meta: {
                            title: children.menuName,
                            parentId: children.parentId,
                        },
                        // 设置组件，
                        component: () => import(/*@vite-ignore */`../views${children.path}.vue`)
                        // component: modules[`../views/${children.path}.vue`]
                    }
                    this.routerList.push(routerInfo)
                })
            })
        },
        // 查询用户菜单，并且生成动态路由数据结构
        generateRouter() {
            return new Promise((resolve,reject) => {
                // 查询用户的菜单
                searchSelfRouter().then(res => {
                    if(res.data.code === 200) {
                        this.setMenuList(res.data.data);
                        this.setRouterList(res.data.data);
                        resolve()
                    }
                    reject()
                })
            })
        },
        updateBreadcrumbList(newList) {
            this.breadcrumbList = newList;
        },
        setButtonPermissions(data) {
            const buttonPermissions = []; // 存储按钮权限的数组

            const processMenu = (menuItems) => {
                if (!menuItems) return;
                menuItems.forEach(item => {
                    // 如果是按钮类型并且权限不为空，则将权限添加到数组中
                    if (item.menuType === 'BUTTON' && item.perms && item.perms !== '') {
                        buttonPermissions.push(item.perms);
                    }
                    // 如果存在子菜单，则递归处理子菜单
                    if (item.children && item.children.length > 0) {
                        processMenu(item.children);
                    }
                });
            };
            processMenu(data);
            this.buttonPermissions = buttonPermissions;
        },
        setCurrentRoute(data) {
            this.currentRoute = data;
        },
    },
    /*为什么使用持久化就不行了，原因是因为只进行了判断是否有数据，并没有判断router里面是否有被addRouter过，
    如果刷新页面后获取到的routerList就直接判断路径是否存在了，实际上这时候router里面只有白名单数据*/
    // 使用持久化
    persist: {
    	enabled: true,
        storage: localStorage,
        key: 'adminMenu',
        path: ['menuList', 'routerList']
    }
})