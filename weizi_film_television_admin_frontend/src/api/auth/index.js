// 请求接口
import request from '@/utils/request.js';

// 登录接口
export function adminLogin(data) {
    return request({
        url: '/admin/auth/login',
        method: "POST",
        data: data
    })
}