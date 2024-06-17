// 存储token
export function setToken(tokenKey,token) {
    return localStorage.setItem(tokenKey,token);
}

// 获取token
export function getToken(tokenKey) {
    return localStorage.getItem(tokenKey);
}
// 移除token
export function removeToken(tokenKey) {
    if(getToken(tokenKey)) {
        return localStorage.removeItem(tokenKey);
    }
    return null;
}