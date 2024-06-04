// 请求接口
import request from '@/utils/request.js';

// 查询管理员列表
export function searchMovieList(data) {
    return request({
        url: '/admin/movie/list',
        method: "GET",
        params: data
    })
}

// 移除菜单
export function removeMovie(movieMd5) {
    return request({
        url: '/admin/movie/delete',
        method: "GET",
        params: {
            movieMd5: movieMd5
        }
    })
}

// 个更新列表头像
export function uploadPoster(formData) {
    return request({
        url: '/admin/movie/uploadPoster',
        method: "POST",
        data: formData,
    })
}

export function getMovieM3u8(movieMd5) {
    return request({
        url: '/admin/movie/getMovieUrl',
        method: "GET",
        params: {
            movieMd5: movieMd5,
        }
    })
}