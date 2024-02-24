package com.weizi.common.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminLoginDto implements Serializable {
    private String account;
    private String password;
    private Integer rememberMe;
}
