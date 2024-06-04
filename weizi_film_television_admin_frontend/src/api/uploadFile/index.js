// 请求接口
import request from '@/utils/request.js';

export function verifyFileNameAndTypeAndGetFileId(data) {
    return request({
        url: 'admin/upload/uploadMovie/verifyFileNameAndTypeAndGetFileId',
        method: "POST",
        data: data
    })
}

export function breakpointUpload(data) {
    return request({
        url: 'admin/upload/uploadMovie/breakpointUpload',
        method: "POST",
        data: data,
    })
}

export function removeFile(data) {
    return request({
        url: 'admin/upload/removeFile',
        method: "POST",
        data: data,
    })
}
