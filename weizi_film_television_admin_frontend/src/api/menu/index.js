// 请求接口
import request from '@/utils/request.js';

// 查询菜单列表
export function searchMenuList(data) {
    return request({
        url: '/admin/menu/list',
        method: "GET",
        params: data
    })
}
// 通过id查询菜单
export function searchMenuById(menuId) {
    return request({
        url: `/admin/menu/getDetail`,
        method: "GET",
        params: {
            menuId: menuId
        }
    })
}
// 保存菜单
export function saveMenu(data) {
    return request({
        url: '/admin/menu/save',
        method: "POST",
        data: data
    })
}
// 更新菜单
export function updateMenu(data) {
    return request({
        url: '/admin/menu/update',
        method: "POST",
        data: data
    })
}
// 移除菜单
export function removeMenu(menuIds) {
    return request({
        url: '/admin/menu/delete',
        method: "POST",
        data: menuIds
    })
}
