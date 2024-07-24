import { http } from "@/utils/http";
import {baseAdminUrlApi} from "@/api/utils";

type Result = {
  code: boolean;
  data?: Array<any>;
  msg?: string;
};

export type MenuResultType = {
  menuId: number;
  parentId: number | 0;
  menuName: string;
  sort: number | 0;
  path: string | null;
  componentPath: string | null;
  icon: string | null;
  menuType: string | 'BUTTON';
  perms: string | null;
  children: MenuResultType[] | null; // 子菜单也是MenuResultType类型的数组
};


export const getAsyncRoutes = () => {
  return http.request<Result>("get", baseAdminUrlApi("menu/self"));
};
