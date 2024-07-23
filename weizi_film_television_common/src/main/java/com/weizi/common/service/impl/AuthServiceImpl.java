package com.weizi.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.dto.AdminLoginDTO;
import com.weizi.common.domain.po.AdminPO;
import com.weizi.common.domain.po.RolePO;
import com.weizi.common.domain.vo.AdminInfoVO;
import com.weizi.common.domain.vo.LoginAdminVO;
import com.weizi.common.domain.vo.RouterVO;
import com.weizi.common.exception.ServiceException;
import com.weizi.common.service.IAuthService;
import com.weizi.common.service.MenuService;
import com.weizi.common.utils.JwtUtils;
import com.weizi.common.utils.imageutils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {
    private final MenuService menuService;
    @Value("${item.avatar-path}")
    private String avatarPath;
    @Value("${superadmin.id}")
    // 超级管理员角色ID
    private Long SUPER_ADMIN_ROLE_ID;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;
    private final ImageUtils imageUtils;
    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtils jwtUtils, ImageUtils imageUtils, MenuService menuService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.imageUtils = imageUtils;
        this.menuService = menuService;
    }

    /**
     * login方法
     */
    @Override
    public AdminInfoVO login(AdminLoginDTO adminLoginDto) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(adminLoginDto.getAccount(), adminLoginDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authentication);
        // 获取用户信息
        LoginAdminVO loginAdmin = (LoginAdminVO) authenticate.getPrincipal();
        if (ObjectUtil.isNull(loginAdmin)) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED, "认证失败");
        }
        log.info("loginAdmin: {}",loginAdmin);
        // 创建token
        String token = jwtUtils.createToken(loginAdmin);
        log.info("token=====>{}",token);
        AdminPO adminPO = loginAdmin.getAdminPO();
        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtil.copyProperties(adminPO,adminInfoVO);
        Optional.ofNullable(adminInfoVO.getAvatar()).ifPresent(avatar -> {
            String extension = FilenameUtils.getExtension(avatar);
            adminInfoVO.setAvatar("data:image/" + extension.toLowerCase() + ";base64," + imageUtils.encodeImageToBase64(avatarPath + avatar));
        });
        adminInfoVO.setToken(token);
        List<Long> longList = adminPO.getRoleList().stream().map(RolePO::getRoleId).toList();
        // 设置路由
        List<RouterVO> routerVOS = menuService.getSelfMenu(ObjectUtil.contains(longList, SUPER_ADMIN_ROLE_ID), adminPO.getAdminId());
        adminInfoVO.setMenus(routerVOS);
        return adminInfoVO;
    }
}
