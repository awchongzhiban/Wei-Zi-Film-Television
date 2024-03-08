package com.weizi.common.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("menu")
public class UmsMenuEntity implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long menuId;
    private Long parentId;
    @NotNull(message = "请填写菜单名")
    private String menuName;
    private String sort;
    @NotNull(message = "请选择菜单类型")
    private String menuType;
    private String path;
    private String componentPath;
    @NotNull(message = "权限不能为空")
    private String perms;
    @NotNull(message = "icon不能为空")
    private String icon;
    // 逻辑删除，Mybatis-Plus里默认0是未删除，1是删除
//    @TableLogic(value = "1", delval = "0") 如果自定义反过来就这样写
    private Boolean status;
    @TableLogic
    private Boolean deleted;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<UmsMenuEntity> children = new ArrayList<>();
}
