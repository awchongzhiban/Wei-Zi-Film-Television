package com.weizi.common.domain.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.weizi.common.domain.po.MenuPO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class MovieDTO implements Serializable {
    private Long movieId;         // 影片ID
    private String movieName;      // 影片名称
    private String fileType;       // 文件类型
    private String fileSize;       // 文件大小
    private Boolean isMerge;       // 是否已合并
    private Boolean isPlayer;      // 是否可播放
    private Boolean status;     // 状态
    private String poster;      // 海报
    private String movieMd5;       // 影片MD5
    private Long adminId;          // 管理员ID
    private int movieShardTotal; // 影片碎片总数
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<Long> GenreIdList = new ArrayList<>();
}
