package com.weizi.common.domain.vo.list;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AdminVO implements Serializable {
    private Long adminId;
    private String username;
    private String nickname;
    private String email;
    private String mobile;
    private Boolean sex;
    private String avatar;
    private Boolean status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
