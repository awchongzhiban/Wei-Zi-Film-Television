package com.weizi.common.domain.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MovieDTO {
    private Long movieId;         // 影片ID
    private String movieName;      // 影片名称
    private String fileType;       // 文件类型
    private Boolean isMerge;       // 是否已合并
    private String posterUrl;      // 海报路径
    private String movieMd5;       // 影片MD5
    private Long adminId;          // 管理员ID
    private int movieShardTotal; // 影片碎片总数
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
