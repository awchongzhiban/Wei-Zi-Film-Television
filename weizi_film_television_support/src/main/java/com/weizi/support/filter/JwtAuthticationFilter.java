package com.weizi.support.filter;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.domain.vo.LoginAdminVO;
import com.weizi.common.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

/**
 * @date AWei
 * @date 2024/02/08
 * 该接口在请求前执行一次，获取request中的数据，其中就在请求头里
 * 获取token，根据token从redis中获取用户信息
 */
@Component
@Slf4j
public class JwtAuthticationFilter extends OncePerRequestFilter {
    @Value("${superadmin.id}")
    private Long superAdminId;

    private final JwtUtils jwtUtils;

    public JwtAuthticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取登录用户
        LoginAdminVO loginAdminVO = (LoginAdminVO) jwtUtils.getLoginAdmin(request);
        // 判断是否为null
        if(ObjectUtil.isNotNull(loginAdminVO)) {
            // 检查用户是否具有调用当前接口的权限
            if (adminHasPermission(loginAdminVO, request)) {
                // 鉴权，跳转的时候需要访问 /index 页面
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(loginAdminVO, null, loginAdminVO.getAuthorities());
                // 将用户信息存储到SecurityContext中，SecurityContext存储到SecurityContextHolder中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        // 放行，交由后边的过滤器执行，如果没有登录，就会被登录拦截器[UsernamePasswordAuthenticationFilter]拦截到
        // /admin/auth/login接口就不需要任何权限
        filterChain.doFilter(request,response);
    }

    // 检查用户是否具有调用当前接口的权限
    private boolean adminHasPermission(LoginAdminVO loginAdminVO, HttpServletRequest request) {
        // 获取用户拥有的权限信息
        Collection<? extends GrantedAuthority> authorities = loginAdminVO.getAuthorities();

        // 获取请求的接口路径
        String requestUri = request.getRequestURI();
        log.info("requestUri===>{}",requestUri);
        if (requestUri.startsWith("/admin/admin/info")
                || requestUri.startsWith("/admin/menu/self")
                || requestUri.startsWith("/admin/admin/logout")
                || loginAdminVO.getId().equals(superAdminId)
                || requestUri.startsWith("/admin/movie/get/m3u8/")
                || requestUri.startsWith("/admin/movie/get/ts/")
        ) return true;
        requestUri = convertPagePathToPermission(requestUri);
        // 遍历用户的权限信息，检查是否存在与请求路径匹配的权限
        for (GrantedAuthority authority : authorities) {
            // 获取权限字符串
            String authorityString = authority.getAuthority();
            // 对比权限与请求路径是否匹配，这里简单地使用 startsWith 进行匹配，可以根据实际情况调整匹配逻辑
            log.info("authorityString===>{}",authorityString);
            if (requestUri.startsWith(authorityString)) {
                return true; // 用户具有调用当前接口的权限
            }
        }
        return false; // 用户没有调用当前接口的权限
    }

    private static String convertPagePathToPermission(String pagePath) {
        // 去掉开头的斜杠
        String trimmedPath = pagePath.startsWith("/") ? pagePath.substring(1) : pagePath;
        // 替换斜杠为冒号
        return trimmedPath.replace("/", ":");
    }
}
