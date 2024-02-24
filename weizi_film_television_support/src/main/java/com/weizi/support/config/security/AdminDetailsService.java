package com.weizi.support.config.security;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.domain.entity.UmsAdminEntity;
import com.weizi.common.domain.entity.UmsMenuEntity;
import com.weizi.common.domain.entity.UmsRoleEntity;
import com.weizi.common.domain.vo.LoginAdminVO;
import com.weizi.common.mapper.UmsAdminMapper;
import com.weizi.common.mapper.UmsMenuMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminDetailsService implements UserDetailsService {

    private final UmsAdminMapper umsAdminMapper;
    private final UmsMenuMapper umsMenuMapper;

    public AdminDetailsService(UmsAdminMapper umsAdminMapper, UmsMenuMapper umsMenuMapper) {
        this.umsAdminMapper = umsAdminMapper;
        this.umsMenuMapper = umsMenuMapper;
    }
    /**
     * 实现方法，在此方法中根据用户名查询用户
     * 账号可能是用户名或者邮箱、手机号等，通过正则表达式判断账号类型（注册的时候用户名做限制就可以避免纯数字或者类似邮箱）
     */
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        log.info("loadUserByUsername=======>{}",account);
        // TODO 验证账号类型
        int accountType = 0;
        // 根据账号查询用户，同时将角色查出来，联查最好别超过三张表
        UmsAdminEntity umsAdmin = umsAdminMapper.selectAdminByUserName(account, accountType);
        log.info("umsAdmin===>{}", umsAdmin);
        // 权限查询是根据角色查询的，首先获取所有角色id
        if (ObjectUtil.isNotNull(umsAdmin)) {
            List<UmsRoleEntity> roleList = umsAdmin.getRoleList();
            // 取出ID
            List<Long> roleIds = roleList.stream().map(UmsRoleEntity::getRoleId).toList();
            log.info("roleIds====>{}", roleIds);
            List<UmsMenuEntity> menuList = umsMenuMapper.selectAdminByRoleIds(roleIds);
            // 查询所有菜单
            List<String> perms = menuList.stream().map(UmsMenuEntity::getPerms).collect(Collectors.toList());
            log.info("权限====>{}", perms);
            umsAdmin.setPerms(perms);
        }
        // 根据角色查询权限
        LoginAdminVO loginAdminVO = new LoginAdminVO();
        loginAdminVO.setUmsAdmin(umsAdmin);
        loginAdminVO.setId(umsAdmin.getAdminId());

        return loginAdminVO;
    }
}
