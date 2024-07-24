// 导入axios和自定义的getToken函数，同时为getToken添加类型注解
import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';
import { getToken } from '@/utils/token/index'; // 假设getToken返回string类型

// 定义可能的HTTP状态码和错误消息类型
type ResponseCode = number;
type ResponseMessage = string;

// 创建axios实例并配置
const request: AxiosInstance = axios.create({
    baseURL: 'http://localhost:9090/user',
    withCredentials: false, // 跨域请求时携带cookie
    timeout: 30000, // 请求超时时间，单位毫秒
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
});

// 配置请求拦截器
request.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    // 添加token到请求头
    const token = getToken('weiziUserToken');
    if (token) {
        config.headers['Weizi-User-Authorization'] = token;
    }
    return config; // 直接返回，无需类型断言
}, (error: AxiosError) => {
    // 错误处理
    return Promise.reject(error);
});

// 配置响应拦截器
request.interceptors.response.use((response: AxiosResponse<{ code: ResponseCode; msg?: ResponseMessage; data?: any }>) => {
    const { code, msg } = response.data;    // 判断响应码，后端返回的数据  code ，data，msg
    if(code == null) {
        return response;
    }else if(code === 200) {
        return response;
    }else if(code == 401) {
        ElMessage.error('没有操作权限！');
    }else if(code == 403) {
        ElMessage.error('登录过期！');
    }
    return Promise.reject(msg);
},(error) => {
    // 出现异常
    ElMessage.error('error=====>',error);
    return Promise.reject(error);
})

export default request;
