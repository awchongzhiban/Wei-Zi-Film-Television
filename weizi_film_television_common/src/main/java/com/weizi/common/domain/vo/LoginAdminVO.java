package com.weizi.common.domain.vo;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.domain.entity.UmsAdminEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
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
    private UmsAdminEntity umsAdmin;

    private long loginTime;

    /**
     * 管理员权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (ObjectUtil.isNull(umsAdmin)) return Collections.emptyList();
        List<String> perms = umsAdmin.getPerms();
        // 判空返回数据
        if (ObjectUtil.isNotEmpty(perms)) {
            // 排除掉权限为空的
            return perms.stream().filter(Objects::nonNull).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return umsAdmin.getPassword();
    }

    @Override
    public String getUsername() {
        return umsAdmin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !umsAdmin.getStatus();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !umsAdmin.getStatus();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !umsAdmin.getStatus();
    }

    @Override
    public boolean isEnabled() {
        return !umsAdmin.getStatus();
    }
}
