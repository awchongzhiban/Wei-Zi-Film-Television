package com.weizi.common.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weizi.common.annotation.NotEmptyOrNullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("menu")
public class MenuPO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long menuId;
    private Long parentId;
    @NotNull(message = "请填写菜单名")
    private String menuName;
    private String sort;
    @NotNull(message = "请选择菜单类型")
    private String menuType;
    @NotEmptyOrNullable(message = "路由路径必须为非空字符串或null")
    private String path;
    @NotEmptyOrNullable(message = "组件路径必须为非空字符串或null")
    private String componentPath;
    @NotEmptyOrNullable(message = "权限标识必须为非空字符串或null")
    private String perms;
    private String icon;
    // 逻辑删除，Mybatis-Plus里默认0是未删除，1是删除
//    @TableLogic(value = "1", delval = "0") 如果自定义反过来就这样写
    private Boolean status;
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<MenuPO> children = new ArrayList<>();
}
