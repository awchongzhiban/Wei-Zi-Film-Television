package com.weizi.common.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @date AWei
 * @date 2024/02/09
 */
@Data
public class RouterVO {
    private Long menuId;
    private Long parentId;
    private String menuName;
    private String menuType;
    private String path;
    private String componentPath;
    private String perms;
    private String icon;
    private Boolean status;
    private Integer sort;
    private List<RouterVO> children = new ArrayList<>();
}
