package com.weizi.common.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("admin")
public class UmsAdminEntity implements Serializable {
    @TableId
    private Long adminId;
    private String username;
    private String nickname;
    private String email;
    private String mobile;
    private Boolean sex;
    private String avatar;
    private String password;
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

    // 角色信息
    @TableField(exist = false)
    private List<UmsRoleEntity> roleList = new ArrayList<>();

    // 菜单信息
    @TableField(exist = false)
    private List<String> perms = new ArrayList<>();
}
