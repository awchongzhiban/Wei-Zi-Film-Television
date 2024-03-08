package com.weizi.common.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.dto.AdminLoginDTO;
import com.weizi.common.domain.vo.LoginAdminVO;
import com.weizi.common.exception.ServiceException;
import com.weizi.common.service.IAuthService;
import com.weizi.common.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {
    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;
    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * login方法
     */
    @Override
    public String login(AdminLoginDTO adminLoginDto) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(adminLoginDto.getAccount(), adminLoginDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authentication);
        // 获取用户信息
        LoginAdminVO loginAdmin = (LoginAdminVO) authenticate.getPrincipal();
        if (ObjectUtil.isNull(loginAdmin)) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED, "认证失败");
        }
        // 创建token
        String token = jwtUtils.createToken(loginAdmin);
        log.info("token=====>{}",token);
        return token;
    }
}
