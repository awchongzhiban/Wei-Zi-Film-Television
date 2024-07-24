import Cookies from "js-cookie";
import { storageLocal } from "@pureadmin/utils";
import { useAdminStoreHook } from "@/store/modules/admin";

export interface DataInfo<T> {
  /** 头像 */
  avatar: string,
  /** 昵称 */
  nickname: string;
  /** token */
  token: string;
}

export const userKey = "admin-info";
export const TokenKey = "authorized-token";
/**
 * 通过`multiple-tabs`是否在`cookie`中，判断用户是否已经登录系统，
 * 从而支持多标签页打开已经登录的系统后无需再登录。
 * 浏览器完全关闭后`multiple-tabs`将自动从`cookie`中销毁，
 * 再次打开浏览器需要重新登录系统
 * */
export const multipleTabsKey = "multiple-tabs";

/** 获取`token` */
export function getToken(): DataInfo<number> {
  // 此处与`TokenKey`相同，此写法解决初始化时`Cookies`中不存在`TokenKey`报错
  return Cookies.get(TokenKey)
    ? JSON.parse(Cookies.get(TokenKey))
    : storageLocal().getItem(userKey);
}

/**
 * @description 设置`token`以及一些必要信息
 */
export function setToken(data: DataInfo<string>) {
  const token = data.token; // 只关注token
  const cookieString = JSON.stringify({ token: token });

  // 设置cookie，不包含过期时间，创建会话cookie
  Cookies.set(TokenKey, cookieString);

  Cookies.set(
    multipleTabsKey,
    "true"
  );

  function setUserKey(nickname: string, avatar: string) {
    useAdminStoreHook().SET_NICKNAME(nickname);
    useAdminStoreHook().SET_AVATAR(avatar);
    storageLocal().setItem(userKey, {
      nickname,
      avatar
    });
  }

  if (data.nickname && data.avatar) {
    const { nickname, avatar } = data;
    setUserKey(nickname, avatar);
  } else {
    const nickname =
      storageLocal().getItem<DataInfo<string>>(userKey)?.nickname ?? "";
    const avatar =
      storageLocal().getItem<DataInfo<string>>(userKey)?.avatar ?? "";
    setUserKey(nickname, avatar);
  }
}


/** 删除`token`以及key值为`user-info`的localStorage信息 */
export function removeToken() {
  Cookies.remove(TokenKey);
  Cookies.remove(multipleTabsKey);
  storageLocal().removeItem(userKey);
}

/** 格式化token（预留，后续根据需要再更改jwt格式） */
export const formatToken = (token: string): string => {
  return token;
};
