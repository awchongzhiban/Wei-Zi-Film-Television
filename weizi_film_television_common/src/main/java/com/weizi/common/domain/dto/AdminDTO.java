package com.weizi.common.domain.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weizi.common.domain.po.RolePO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("admin")
public class AdminDTO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long adminId;
    @NotNull(message = "用户名不能为空")
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String mobile;
    @NotNull(message = "密码不能为空")
    private String password;
    private Boolean status;
    @NotNull(message = "创建者ID不能为空")
    private Long parentAdminId;
    // 逻辑删除，Mybatis-Plus里默认0是未删除，1是删除
//    @TableLogic(value = "1", delval = "0") 如果自定义反过来就这样写
    /*@TableLogic
    private Boolean deleted;*/
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<Long> roleIdList;
}
