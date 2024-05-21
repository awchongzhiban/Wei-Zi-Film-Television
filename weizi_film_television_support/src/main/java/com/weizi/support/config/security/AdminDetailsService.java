package com.weizi.support.config.security;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.domain.po.AdminPO;
import com.weizi.common.domain.po.MenuPO;
import com.weizi.common.domain.po.RolePO;
import com.weizi.common.domain.vo.LoginAdminVO;
import com.weizi.common.mapper.AdminMapper;
import com.weizi.common.mapper.MenuMapper;
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

    private final AdminMapper adminMapper;
    private final MenuMapper menuMapper;

    public AdminDetailsService(AdminMapper adminMapper, MenuMapper menuMapper) {
        this.adminMapper = adminMapper;
        this.menuMapper = menuMapper;
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
        AdminPO adminPO = adminMapper.selectAdminByUserName(account, accountType);
        log.info("adminPO===>{}", adminPO);
        // 权限查询是根据角色查询的，首先获取所有角色id
        if (ObjectUtil.isNotNull(adminPO)) {
            List<RolePO> roleList = adminPO.getRoleList();
            // 取出ID
            List<Long> roleIds = roleList.stream().map(RolePO::getRoleId).toList();
            log.info("roleIds====>{}", roleIds);
            List<MenuPO> menuList = menuMapper.selectAdminByRoleIds(roleIds);
            // 查询所有菜单
            List<String> perms = menuList.stream().map(MenuPO::getPerms).collect(Collectors.toList());
            log.info("权限====>{}", perms);
            adminPO.setPerms(perms);
        }
        // 根据角色查询权限
        LoginAdminVO loginAdminVO = new LoginAdminVO();
        loginAdminVO.setAdminPO(adminPO);
        loginAdminVO.setId(adminPO.getAdminId());

        return loginAdminVO;
    }
}
