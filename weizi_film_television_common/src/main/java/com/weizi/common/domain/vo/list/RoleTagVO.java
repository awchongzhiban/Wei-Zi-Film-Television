package com.weizi.common.domain.vo.list;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RoleTagVO implements Serializable {
    private Long roleId;
    private String roleName;
}
