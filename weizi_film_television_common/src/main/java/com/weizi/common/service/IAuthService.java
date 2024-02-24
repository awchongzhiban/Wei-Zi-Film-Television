package com.weizi.common.service;

import com.weizi.common.domain.dto.AdminLoginDto;

public interface IAuthService {
    String login(AdminLoginDto adminLoginDto);
}
