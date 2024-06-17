package com.weizi.common.domain.dto.pageParam;

import com.weizi.common.request.PageParam;
import lombok.Data;

/**
 * @date AWei
 * @date 2024/02/12
 */
@Data
public class RoleParamDTO extends PageParam {
    private String roleLabel;
    private String roleName;
}
