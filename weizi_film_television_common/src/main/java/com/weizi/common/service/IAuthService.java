package com.weizi.common.service;

import com.weizi.common.domain.dto.AdminLoginDTO;

public interface IAuthService {
    String login(AdminLoginDTO adminLoginDto);
}
