package com.weizi.common.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("admin")
public class AdminPO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long adminId;
    private String username;
    private String nickname;
    private String email;
    private String mobile;
    private Boolean sex;
    private String avatar;
    private String password;
    private Boolean status;
    private Long parentAdminId;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 角色信息
    @TableField(exist = false)
    private List<RolePO> roleList = new ArrayList<>();

    // 菜单信息
    @TableField(exist = false)
    private List<String> perms = new ArrayList<>();
}
