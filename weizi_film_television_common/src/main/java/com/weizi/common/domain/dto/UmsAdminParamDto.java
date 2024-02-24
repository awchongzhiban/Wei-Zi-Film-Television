package com.weizi.common.domain.dto;

import com.weizi.common.request.PageParam;
import lombok.Data;

/**
 * @date AWei
 * @date 2024/02/12
 */
@Data
public class UmsAdminParamDto extends PageParam {

    private String username;
    private String nickname;
    private String email;
    private String mobile;
}
