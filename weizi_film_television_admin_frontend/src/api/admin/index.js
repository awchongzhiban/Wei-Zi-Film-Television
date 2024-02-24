// 请求接口
import request from '@/utils/request.js';

// 登录当前用户的路由和接口
export function searchSelfRouter() {

    return request({
        url: '/admin/menu/self',
        method: "GET"
    })
}

// 查询个人信息
export function searchSelfInfo() {

    return request({
        url: '/admin/admin/info',
        method: "GET"
    })
}

// 查询管理员列表
export function searchAdminList(data) {
    return request({
        url: '/admin/admin/list',
        method: "GET",
        params: data
    })
}
// 通过id查询菜单
export function searchAdminById(adminId) {
    return request({
        url: `/admin/admin/getDetail`,
        method: "GET",
        params: {
            adminId: adminId
        }
    })
}
// 保存菜单
export function saveAdmin(data) {
    return request({
        url: '/admin/admin/save',
        method: "POST",
        params: data
    })
}
// 更新菜单
export function updateAdmin(data) {
    return request({
        url: '/admin/admin/update',
        method: "POST",
        params: data
    })
}
// 移除菜单
export function removeAdmin(adminId) {
    return request({
        url: '/admin/admin/delete',
        method: "POST",
        data: adminId
    })
}

// 个更新列表头像
export function uploadAvatar(formData) {
    return request({
        url: '/admin/admin/uploadAvatar',
        method: "POST",
        data: formData,
        responseType: 'blob'
    })
}