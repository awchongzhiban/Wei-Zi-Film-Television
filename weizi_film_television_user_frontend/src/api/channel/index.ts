// 请求接口
import request from '@/utils/request.ts';

// 获取频道列表
export function getChannelList() {
    return request({
        url: '/channel/getChannelList',
        method: "GET"
    })
}

export function getHotTags() {
    return request({
        url: '/channel/getHotTags',
        method: "GET"
    })
}

// 获取综合频道影片列表
export function getMovieChannelList(params: any) {
    return request({
        url: '/channel/getMovieChannelList',
        method: "GET",
        params
    })
}