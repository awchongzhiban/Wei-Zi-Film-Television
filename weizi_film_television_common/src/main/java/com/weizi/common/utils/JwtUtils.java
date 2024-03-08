package com.weizi.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.weizi.common.constants.CacheConstants;
import com.weizi.common.domain.vo.LoginAdminVO;
import com.weizi.common.utils.redis.RedisCacheUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 通过JWT生成Token、解析Token和刷新Token
 */
@Component
@Slf4j
public class JwtUtils {
    private String secret = "weizivisiontechnologycompany";

    @Autowired
    private RedisCacheUtil redisCacheUtil;
    /**
     * 创建token，会将用户数据存放到redis中
     * 可以方便的四线单点登录，实现踢人下线，查看在线用户等等功能
     * 可以使用UUID当做redis的key
     */
    public String createToken(LoginAdminVO loginAdminVO) {
        // 创建一个map
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        // 将UUID存储到登录用户中，可以在后台系统中根据token值获取redis中数据
        loginAdminVO.setToken(token);
        // 设置登录时间
        loginAdminVO.setLoginTime(System.currentTimeMillis());
        Map<String, Object> claims = new HashMap<>();
        claims.put("token", token);
        // 将用户数据缓存到redis
        log.info("redis token=====>{}",CacheConstants.LOGIN_ADMIN_KEY + token);
        String redisKey = CacheConstants.LOGIN_ADMIN_KEY + token;
        redisCacheUtil.setCacheObject(redisKey, loginAdminVO, 30, TimeUnit.MINUTES);
        // 调用刷新token方法
        refreshToken(loginAdminVO);
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }
    /**
     * 解析token
     * token: jwt字符串*****.****.***
     */
    public Claims parseToken(String token) {
        // 解析token
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取登录用户
     * 根据token，解析之后从redis中获取
     * 并且刷新token
     */
    public Object getLoginAdmin(HttpServletRequest request) {
        // 通过jwt加密过的
        String token = request.getHeader("Weizi-Authorization");
        if(StrUtil.isNotEmpty(token)) {
            // 解析token
            Claims claims = parseToken(token);
            String parseToken = (String) claims.get("token");
            String redisKey = CacheConstants.LOGIN_ADMIN_KEY + parseToken;
            // 从redis中获取数据
            LoginAdminVO loginAdminVO = redisCacheUtil.getCacheObject(redisKey);
            if (ObjectUtil.isNull(loginAdminVO)) return null;
            // 获取登录时间
            long loginTime = loginAdminVO.getLoginTime();
            // 获取当前时间
            long currentTimeMillis = System.currentTimeMillis();
            // 判断是否相差20分钟
            long millis = currentTimeMillis / 1000 / 60 - loginTime/ 1000 / 60;
            if(millis >= 20) {
                refreshToken(loginAdminVO);
            }
            return loginAdminVO;
        }
        return null;
    }
    // 刷新token
    private void refreshToken(LoginAdminVO loginAdminVO) {
        // 将用户数据缓存到redis中
        redisCacheUtil.setCacheObject(CacheConstants.LOGIN_ADMIN_KEY + loginAdminVO.getToken(),loginAdminVO,30, TimeUnit.MINUTES);
    }

    public void clearToken(String token) {
        if(StrUtil.isNotEmpty(token)) {
            // 解析token
            Claims claims = parseToken(token);
            String parseToken = (String) claims.get("token");
            String redisKey = CacheConstants.LOGIN_ADMIN_KEY + parseToken;
            // 从 Redis 中删除指定的 key
            redisCacheUtil.deleteObject(redisKey);
        }
    }
}
