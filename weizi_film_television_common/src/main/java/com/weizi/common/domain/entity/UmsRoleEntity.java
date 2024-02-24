package com.weizi.common.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("role")
public class UmsRoleEntity implements Serializable {
    @TableId
    private Long roleId;
    private String roleLabel;
    private String roleName;
    private String sort;
    private Boolean status;
    // 逻辑删除，Mybatis-Plus里默认0是未删除，1是删除
//    @TableLogic(value = "1", delval = "0") 如果自定义反过来就这样写
    @TableLogic
    private Boolean deleted;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
