package com.weizi.common.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("channel")
public class ChannelPO {
    @TableId(type = IdType.AUTO)
    private Long channelId; // 频道ID
    private String channelName; // 频道名称
    private String path; // 路由
    private Boolean isShowChoiceTag; // 是否显示首页标签
    private Boolean isActive; // 是否激活
    private Integer sortOrder; // 排序
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
