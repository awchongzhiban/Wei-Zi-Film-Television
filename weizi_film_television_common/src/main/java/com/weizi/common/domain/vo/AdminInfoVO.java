package com.weizi.common.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @date AWei
 * @date 2024/02/12
 */
@Data
public class AdminInfoVO implements Serializable {

    private Long adminId;
    private String nickname;
    private String avatar;
}
