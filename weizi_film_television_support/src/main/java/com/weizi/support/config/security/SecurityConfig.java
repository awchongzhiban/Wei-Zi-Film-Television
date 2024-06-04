package com.weizi.support.config.security;

import com.alibaba.fastjson2.JSONObject;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.utils.JwtUtils;
import com.weizi.support.filter.JwtAuthticationFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 引入AdminDetailsService
    private final AdminDetailsService adminDetailsService;

    private final JwtAuthticationFilter jwtAuthticationFilter;

    private final JwtUtils jwtUtils;

    public SecurityConfig(AdminDetailsService adminDetailsService, JwtAuthticationFilter jwtAuthticationFilter, JwtUtils jwtUtils) {
        this.adminDetailsService = adminDetailsService;
        this.jwtAuthticationFilter = jwtAuthticationFilter;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 禁用csrf
        http.csrf(AbstractHttpConfigurer::disable);
        // 配置拦截策略
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/admin/auth/login").permitAll()
                .requestMatchers("/admin/movie/get/**").permitAll() // 这个路由是为了配合电影播放设置的，请求方面使用
                .anyRequest().authenticated());
        // 开启form认证
        http.formLogin(Customizer.withDefaults());
        // 配置跨域
        http.cors(cors -> cors.configurationSource(configurationSource()));
        // 配置过滤器
        http.addFilterBefore(jwtAuthticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(this::onAuthenticationFailure));
        http.logout(logout -> logout
                        .logoutUrl("/admin/auth/logout") // 指定注销 URL
                        .logoutSuccessHandler(this::onLogoutSuccess)); // 指定注销成功处理器
        return http.build();
    }



    // 创建AuthenticationManager
    @Bean
    public AuthenticationManager adminAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(adminDetailsService);
        return new ProviderManager(provider);
    }

    // 配置密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 配置跨域，允许跨域 配置CorsConfigurationSource
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));

        // 创建 CorsConfigurationSource对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(WeiZiResult.error(HttpServletResponse.SC_FORBIDDEN, "请重新登录！")));
    }

    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 例如，清除 session
        request.getSession().invalidate();
        // 通过jwt加密过的
        String token = request.getHeader("Weizi-Authorization");
        // 调用清除 token 的方法
        jwtUtils.clearToken(token);
        response.getWriter().write(JSONObject.toJSONString(WeiZiResult.success()));
    }

}
