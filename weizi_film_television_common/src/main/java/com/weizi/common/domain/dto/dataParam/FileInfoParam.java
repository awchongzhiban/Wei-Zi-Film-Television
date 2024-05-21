package com.weizi.common.domain.dto.dataParam;

import com.weizi.common.annotation.ValidFileType;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileInfoParam {
    @NotNull(message = "文件名不能为空")
    private String fileName;
    @NotNull(message = "文件类型不能为空")
//    @ValidFileType
    private String fileType;
    @NotNull(message = "文件大小不能为空")
    private String fileSize;
    @NotNull(message = "影片MD5不能为空")
    private String movieMd5;
    @NotNull(message = "影片切片总数不能为空")
    private int movieShardTotal;
}
