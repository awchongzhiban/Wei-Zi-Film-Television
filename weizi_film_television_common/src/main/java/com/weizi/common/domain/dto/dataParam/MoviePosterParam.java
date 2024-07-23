package com.weizi.common.domain.dto.dataParam;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class MoviePosterParam {
    @NotNull(message = "movieMd5不可为空")
    private String movieMd5;
    @NotNull(message = "poster不可为空")
    private String poster;
}