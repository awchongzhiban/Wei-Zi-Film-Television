package com.weizi.common.domain.dto.dataParam;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MovieParam implements Serializable {
    private Long movieId;       // 影片ID
    private String movieName;   // 影片名称
    private String movieMd5;    // 影片md5
    private Boolean status;     // 影片状态
    private Long channelId;     // 频道ID
    private String mainPoster;      // 主海报
    private String poster;      // 海报
    private List<Long> genreIdList; // 影片类型ID列表
}
