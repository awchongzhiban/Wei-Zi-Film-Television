// 请求接口
import request from '@/utils/request.js';

// 登录接口
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