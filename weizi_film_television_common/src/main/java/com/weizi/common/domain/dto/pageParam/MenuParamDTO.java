package com.weizi.common.domain.dto.pageParam;

import com.weizi.common.request.PageParam;
import lombok.Data;

/**
 * @date AWei
 * @date 2024/02/12
 * description 不需要分页参数
 */
@Data
public class MenuParamDTO {
    private String menuName;
    private String perms;
}
