import { http } from "@/utils/http";
import {baseAdminUrlApi} from "@/api/utils";

type Result = {
  code: boolean;
  data?: Array<any>;
  msg?: string;
};

type MenuResultType = {
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

/** 获取菜单管理列表 */
export const getMenuList = (data?: object) => {
  return http.request<Result>("get", baseAdminUrlApi("menu/list"), { data });
};

/** 新增菜单 */
export const saveMenu = (data?: object) => {
  return http.request<Result>("post", baseAdminUrlApi("menu/save"), { data });
};

/** 更新菜单 */
export const updateMenu = (data?: object) => {
  return http.request<Result>("post", baseAdminUrlApi("menu/update"), { data });
};

/** 删除菜单 */
export const deleteMenu = (menuId?: number) => {
  let params = {
    menuId: menuId
  }
  return http.request<Result>("post", baseAdminUrlApi("menu/delete"), { params });
};
