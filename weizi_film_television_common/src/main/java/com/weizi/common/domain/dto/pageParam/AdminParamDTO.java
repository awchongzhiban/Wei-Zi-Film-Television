package com.weizi.common.domain.dto.pageParam;

import com.weizi.common.request.PageParam;
import lombok.Data;

/**
 * @date AWei
 * @date 2024/02/12
 */
@Data
public class AdminParamDTO extends PageParam {

    private String username;
    private String nickname;
    private String email;
    private String mobile;
}
