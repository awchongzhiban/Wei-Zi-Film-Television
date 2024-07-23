package com.weizi.auth.controller;

import com.weizi.common.domain.dto.AdminLoginDTO;
import com.weizi.common.domain.vo.AdminInfoVO;
import com.weizi.common.domain.vo.LoginAdminVO;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.IAuthService;
import com.weizi.common.utils.redis.AdminTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口
 */
@RestController
@RequestMapping("admin/auth")
@Slf4j
public class AuthAdminController {
    private final IAuthService authService;

    public AuthAdminController(IAuthService authService) {
        this.authService = authService;
    }

    /**
     * 系统用户登录
     */
    @RequestMapping("login")
    public WeiZiResult login(@RequestBody AdminLoginDTO adminLoginDto) {
        log.info("adminLoginDto====》{}", adminLoginDto);
        AdminInfoVO adminInfoVO = authService.login(adminLoginDto);
        return WeiZiResult.success(adminInfoVO);
    }
}
