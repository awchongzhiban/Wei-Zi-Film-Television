// 封装axios，做请求处理
// 导入axios
import axios from 'axios';
import {getToken} from "@/utils/token/index.js";
import router from "@/router/index.js";
// 创建axios
const request = axios.create({
    // 根请求地址
    baseURL: 'http://localhost:9090',
    withCredentials: false,// 用于配置请求接口跨域时是否需要凭证
    timeout: 30000 // 超时时间，单位毫秒
})

// 配置请求头的参数类型，和编码格式
axios.defaults.headers['Content-Type'] = 'application/json?chatset=utf-8'
// 配置请求的拦截器
request.interceptors.request.use((config) => {

    // 在请求头添加token, 判断是否需要发送token
    if(getToken("weiziToken")) {
        config.headers['Weizi-Authorization'] = getToken("weiziToken");
    }
    return config;

},(error) => {
    // 发生异常
    console.log("请求异常====》",error);
    return Promise.reject(error);
})

// 配置响应拦截器
request.interceptors.response.use((response) => {
    // 判断响应码，后端返回的数据  code ，data，msg
    let {msg,code} = response.data
    if(code == null) {
        return response;
    }else if(code == 200) {
        return response;
    }else if(code == 401) {
        ElMessage.error('没有操作权限！');
    }else if(code == 403) {
        ElMessage.error('登录过期！');
        // 需要重新登陆，跳转到登录页面，清除pinia中的数据，sessionStorage中
        window.sessionStorage.clear();
        router.push('/admin-login');
    }
    return Promise.reject(msg);
},(error) => {
    // 出现异常
    ElMessage.error('error=====>',error);
    window.sessionStorage.clear();
    router.push('/admin-login');
    return Promise.reject(error);
})

// 导出
export default request;