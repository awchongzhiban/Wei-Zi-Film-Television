package com.weizi.common.domain.po;

import lombok.Data;

@Data
public class AdminTreeNode {
    private Long adminId; // 管理员ID
    private Long parentAdminId; // 父级管理员ID
}
