package com.weizi.common.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("movie")
public class MoviePO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long movieId;         // 影片ID
    private String movieName;      // 影片名称
    private String fileType;       // 文件类型
    private String fileSize;       // 文件大小
    private Boolean isMerge;       // 是否已合并
    private Boolean isPlayer;      // 是否可播放
    private Boolean status;     // 状态        \
    private String mainPoster;      // 主海报
    private String poster;      // 海报
    private String movieMd5;       // 影片MD5
    private Long adminId;          // 管理员ID
    private int movieShardTotal; // 影片碎片总数
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
