package com.weizi.common.domain.dto.dataParam;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("admin")
public class AdminDTO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long adminId;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String mobile;
    private Boolean sex;
    private String password;
    private Boolean status;
    // 逻辑删除，Mybatis-Plus里默认0是未删除，1是删除
//    @TableLogic(value = "1", delval = "0") 如果自定义反过来就这样写
    @TableLogic
    private Boolean deleted;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
