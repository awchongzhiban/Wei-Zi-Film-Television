// 请求接口
import request from '@/utils/request.js';

// 查询影片列表
export function searchMovieList(data) {
    return request({
        url: '/admin/movie/list',
        method: "GET",
        params: data
    })
}
// 查询影片列表
export function getGenreTagMap() {
    return request({
        url: '/admin/movie/list/getGenreTagMap',
        method: "GET",
    })
}

// 通过id查询影片
export function getDetail(movieId) {
    return request({
        url: `/admin/movie/getDetail`,
        method: "GET",
        params: {
            movieId: movieId
        }
    })
}

// 更新影片信息
export function update(data) {
    return request({
        url: '/admin/movie/update',
        method: "POST",
        data: data
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