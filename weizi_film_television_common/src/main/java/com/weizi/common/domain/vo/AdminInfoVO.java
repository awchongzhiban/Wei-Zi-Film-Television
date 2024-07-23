package com.weizi.common.domain.vo;

import com.weizi.common.domain.vo.list.RoleTagVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @date AWei
 * @date 2024/02/12
 */
@Data
public class AdminInfoVO {
    private Long adminId;
    private String nickname;
    private String avatar;
    private String token;
    private List<RouterVO> menus;
}
