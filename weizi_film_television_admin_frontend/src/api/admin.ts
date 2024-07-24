import { http } from "@/utils/http";
import { baseAdminUrlApi } from "./utils"

export type UserResult = {
  data: {
    /** 用户名 */
    username: string;
    /** 头像 */
    avatar: string,
    /** `token` */
    token: string;
  };
};

/** 登录 */
export const getLogin = (data?: object) => {
  return http.request<UserResult>("post", baseAdminUrlApi("auth/login"), { data });
};
