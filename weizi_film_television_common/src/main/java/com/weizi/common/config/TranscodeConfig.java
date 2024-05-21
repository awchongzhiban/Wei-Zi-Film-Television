package com.weizi.common.config;

import lombok.Data;

/**
 * @author AWei
 * @create 2024-04-16 21:18
 */
@Data
public class TranscodeConfig {
    private String poster = "00:00:00.001";				// 截取封面的时间			HH:mm:ss.[SSS]
    private String tsSeconds = "15";			// ts分片大小，单位是秒
    private String cutStart;			// 视频裁剪，开始时间		HH:mm:ss.[SSS]
    private String cutEnd;				// 视频裁剪，结束时间		HH:mm:ss.[SSS]
}


