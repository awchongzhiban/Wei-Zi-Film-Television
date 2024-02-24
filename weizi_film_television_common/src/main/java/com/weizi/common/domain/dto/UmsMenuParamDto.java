package com.weizi.common.domain.dto;

import com.weizi.common.request.PageParam;
import lombok.Data;

/**
 * @date AWei
 * @date 2024/02/12
 */
@Data
public class UmsMenuParamDto extends PageParam {

    private String menuName;
    private String perms;
}
