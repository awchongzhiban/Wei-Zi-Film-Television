// 请求接口
import request from '@/utils/request.js';

// 查询管理员列表
export function searchRoleList(data) {
    return request({
        url: '/admin/role/list',
        method: "GET",
        params: data
    })
}
// 通过id查询菜单
export function searchRoleById(roleId) {
    return request({
        url: `/admin/role/getDetail`,
        method: "GET",
        params: {
            roleId: roleId
        }
    })
}
// 保存菜单
export function saveRole(data) {
    return request({
        url: '/admin/role/save',
        method: "POST",
        data: data
    })
}
// 更新菜单
export function updateRole(data) {
    return request({
        url: '/admin/role/update',
        method: "POST",
        data: data
    })
}
// 移除菜单
export function removeRole(roleId) {
    return request({
        url: '/admin/role/delete',
        method: "GET",
        params: {
            roleId: roleId
        }
    })
}