package com.weizi.common.utils.security;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.po.RolePO;
import com.weizi.common.domain.vo.LoginAdminVO;
import com.weizi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @date WeiZi
 * @date 2024/02/09
 * 获取登陆的用户信息
 */
@Component
public class WeiZiSecurityUtil {
    private static Long superAdminId;

    @Autowired
    public void setSuperAdminId(ApplicationContext context) {
        // 使用ApplicationContext获取配置值
        superAdminId = Long.parseLong(Objects.requireNonNull(context.getEnvironment().getProperty("superadmin.id")));
    }
    /**
     * 获取  Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户
     */
    public static LoginAdminVO getLoginAdmin() {
        return (LoginAdminVO) getAuthentication().getPrincipal();
    }

    /**
     * 获取用户id
     */
    public static Long getAdminId() {
        Long adminId = getLoginAdmin().getId();
        if(ObjectUtil.isNull(adminId)) {
            throw new ServiceException(HttpStatus.FORBIDDEN,"");
        }
        return adminId;
    }

    public static List<Long> getRoleIdList() {
        List<Long> roleIdList = getLoginAdmin().getAdminPO().getRoleList().stream().map(RolePO::getRoleId).collect(Collectors.toList());
        if(ObjectUtil.isEmpty(roleIdList)) {
            throw new ServiceException(HttpStatus.FORBIDDEN,"");
        }
        return roleIdList;
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        String username = getLoginAdmin().getUsername();
        if(ObjectUtil.isNull(username)) {
            throw new ServiceException(HttpStatus.FORBIDDEN,"");
        }
        return username;
    }

    public static boolean isSuperAdmin() {
        return WeiZiSecurityUtil.getAdminId().equals(superAdminId);
    }

}
