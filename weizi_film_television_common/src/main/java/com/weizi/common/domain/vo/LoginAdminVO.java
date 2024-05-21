package com.weizi.common.domain.vo;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.domain.po.AdminPO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class LoginAdminVO implements UserDetails {
    private Long id;

    private String token;

    // 管理员信息
    private AdminPO adminPO;

    private long loginTime;

    /**
     * 管理员权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (ObjectUtil.isNull(adminPO)) return Collections.emptyList();
        List<String> perms = adminPO.getPerms();
        // 判空返回数据
        if (ObjectUtil.isNotEmpty(perms)) {
            // 排除掉权限为空的
            return perms.stream().filter(Objects::nonNull).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return adminPO.getPassword();
    }

    @Override
    public String getUsername() {
        return adminPO.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !adminPO.getStatus();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !adminPO.getStatus();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !adminPO.getStatus();
    }

    @Override
    public boolean isEnabled() {
        return !adminPO.getStatus();
    }
}
