import { defineStore } from "pinia";
import { store } from "@/store";
import type { userType } from "./types";
import {routerArrays} from "@/layout/types";
import { router, resetRouter } from "@/router";
import { storageLocal } from "@pureadmin/utils";
import { getLogin } from "@/api/admin";
import type { UserResult } from "@/api/admin";
import { useMultiTagsStoreHook } from "@/store/modules/multiTags";
import {type DataInfo, setToken, removeToken, userKey, getToken} from "@/utils/auth";

export const useAdminStore = defineStore({
  id: "weizi-admin",
  state: (): userType => ({
    // 昵称
    nickname: storageLocal().getItem<DataInfo<number>>(userKey)?.nickname ?? "",
    // 头像
    avatar: storageLocal().getItem<DataInfo<string>>(userKey)?.avatar ?? null,
    // 前端生成的验证码（按实际需求替换）
    verifyCode: "",
    // 判断登录页面显示哪个组件（0：登录（默认）、1：手机登录、2：二维码登录、3：注册、4：忘记密码）
    currentPage: 0,
  }),
  actions: {
    /** 存储用户名 */
    SET_NICKNAME(nickname: string) {
      this.nickname = nickname;
    },
    /** 储存头像 */
    SET_AVATAR(avatar: string) {
      this.avatar = avatar;
    },
    /** 存储前端生成的验证码 */
    SET_VERIFYCODE(verifyCode: string) {
      this.verifyCode = verifyCode;
    },
    /** 存储登录页面显示哪个组件 */
    SET_CURRENTPAGE(value: number) {
      this.currentPage = value;
    },
    /** 登入 */
    async loginByAccount(data) {
      return new Promise<UserResult>((resolve, reject) => {
        getLogin(data)
          .then(res => {
            if (res.code === 200) {
              setToken(res.data);
              resolve(res);
            }
          })
          .catch(error => {
            reject(error);
          });
      });
    },
    /** 前端登出（不调用接口） */
    logOut() {
      this.nickname = "";
      this.avatar = null;
      removeToken();
      useMultiTagsStoreHook().handleTags("equal", [...routerArrays]);
      resetRouter();
      router.push("/login");
    }
  }
});

export function useAdminStoreHook() {
  return useAdminStore(store);
}
