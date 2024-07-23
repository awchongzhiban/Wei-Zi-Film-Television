package com.weizi.common.domain.dto.pageParam;

import com.weizi.common.request.PageParam;
import lombok.Data;

@Data
public class ChannelParamDTO extends PageParam {
    private Long channelId;
}
