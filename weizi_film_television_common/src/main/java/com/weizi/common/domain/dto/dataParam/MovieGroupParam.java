package com.weizi.common.domain.dto.dataParam;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class MovieGroupParam {
    @NotNull(message = "status不可为空！")
    private Boolean status;
    @NotNull(message = "channelId不可为空！")
    private Long channelId;
    @NotNull(message = "movieIdList不可为空！")
    private List<Long> movieIdList;
}