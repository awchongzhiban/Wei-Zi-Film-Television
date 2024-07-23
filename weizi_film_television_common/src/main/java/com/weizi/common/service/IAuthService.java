package com.weizi.common.service;

import com.weizi.common.domain.dto.AdminLoginDTO;
import com.weizi.common.domain.vo.AdminInfoVO;

public interface IAuthService {
    AdminInfoVO login(AdminLoginDTO adminLoginDto);
}
