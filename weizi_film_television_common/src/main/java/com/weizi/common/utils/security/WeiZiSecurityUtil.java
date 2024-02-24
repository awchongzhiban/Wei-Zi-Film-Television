package com.weizi.common.utils.security;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.vo.LoginAdminVO;
import com.weizi.common.exception.ServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @date WeiZi
 * @date 2024/02/09
 * 获取登陆的用户信息
 */
public class WeiZiSecurityUtil {

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
        Long userId = getLoginAdmin().getId();
        if(ObjectUtil.isNull(userId)) {
            throw new ServiceException(HttpStatus.FORBIDDEN,"");
        }
        return userId;
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

}
