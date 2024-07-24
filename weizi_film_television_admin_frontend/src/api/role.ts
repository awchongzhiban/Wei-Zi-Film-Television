import { http } from "@/utils/http";
import {baseAdminUrlApi} from "@/api/utils";

type Result = {
  code: boolean;
  data?: Array<any>;
  msg?: string;
};

type RoleResultType = {
  roleId: number;
  roleLabel: string;
  roleName: string;
  sort: string;
  status: boolean;
  remark: string;
  createTime: Date;
};

/** 获取角色管理列表 */
export const getRoleList = (params?: object) => {
  return http.request<Result>("get", baseAdminUrlApi("role/list"), { params });
};

/** 新增角色 */
export const saveRole = (data?: object) => {
  return http.request<Result>("post", baseAdminUrlApi("role/save"), { data });
};

/** 更新角色 */
export const updateRole = (data?: object) => {
  return http.request<Result>("post", baseAdminUrlApi("role/update"), { data });
};

/** 删除角色 */
export const deleteRole = (menuId?: number) => {
  let params = {
    menuId: menuId
  }
  return http.request<Result>("post", baseAdminUrlApi("role/delete"), { params });
};
