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
public class RouterVO implements Serializable {

    private Long menuId;
    private Long parentId;
    private String menuName;
    private String path;
    private String componentPath;
    private String icon;
    private String menuType;
    private String perms;
    private List<RouterVO> children = new ArrayList<>();
}
