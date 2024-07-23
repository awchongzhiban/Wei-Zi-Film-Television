package com.weizi.common.domain.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminLoginDTO implements Serializable {
    private String account;
    private String password;
    private Boolean rememberMe;
}
