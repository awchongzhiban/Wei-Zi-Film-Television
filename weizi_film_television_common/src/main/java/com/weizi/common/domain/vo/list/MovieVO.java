package com.weizi.common.domain.vo.list;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MovieVO {
    private Long movieId;         // 影片ID
    private String movieName;      // 影片名称
    private String fileSize;       // 文件大小
    private Boolean isMerge;       // 是否已合并
    private Boolean isPlayer;      // 是否可播放
    private String posterUrl;      // 海报路径
    private String movieMd5;       // 影片MD5
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
