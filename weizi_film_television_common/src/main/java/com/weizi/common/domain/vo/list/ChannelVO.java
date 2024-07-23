package com.weizi.common.domain.vo.list;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChannelVO {
    private Long channelId; // 频道ID
    private String channelName; // 频道名称
    private String path; // 路由
    private Boolean isShowChoiceTag; // 是否显示首页标签
    private Integer sortOrder; // 排序
}
