package com.weizi.common.domain.dto;

import lombok.Data;

@Data
public class MovieToGenreDTO {
    private Long genreId;
    private String genreName;
    private Long movieId;
}
