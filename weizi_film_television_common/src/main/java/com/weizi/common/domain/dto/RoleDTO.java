package com.weizi.common.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("role")
public class RoleDTO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long roleId;
    private String roleLabel;
    private String roleName;
    private String sort;
    private Boolean status;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    private Long parentRoleId;
}
