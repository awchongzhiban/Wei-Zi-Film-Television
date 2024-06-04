package com.weizi.common.domain.dto.pageParam;

import com.weizi.common.request.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @date AWei
 * @date 2024/05/21
 */
@Data
public class MovieParamDTO extends PageParam {
    private String movieName;
}
