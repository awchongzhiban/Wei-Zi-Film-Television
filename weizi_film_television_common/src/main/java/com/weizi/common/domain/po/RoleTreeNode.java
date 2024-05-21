package com.weizi.common.domain.po;

import lombok.Data;

@Data
public class RoleTreeNode {
    private Long roleId; // 角色ID
    private String roleName; // 角色名称
    private Long parentRoleId; // 父级角色ID
}
