package com.weizi.common.domain.vo.list;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MovieVO {
    private Long movieId;         // 影片ID
    private String movieName;      // 影片名称
    private String fileSize;       // 文件大小
    private Boolean isMerge;       // 是否已合并
    private Boolean isPlayer;      // 是否可播放
    private Boolean status;     // 状态
    private Long channelId;     // 频道ID
    private String mainPoster;      // 主海报
    private String poster;      // 海报
    private String movieMd5;       // 影片MD5
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<Long> GenreIdList = new ArrayList<>();
}
