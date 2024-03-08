package com.weizi;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.domain.dto.pageParam.RoleParamDTO;
import com.weizi.common.domain.vo.list.RoleVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.IUmsRoleService;
import org.springframework.web.bind.annotation.*;

/**
 * @date AWei
 * @date 2024/02/26
*/
@RestController
@RequestMapping("admin/role")
public class RoleController {

    private final IUmsRoleService roleService;

    public RoleController(IUmsRoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取管理员列表
     */
    @GetMapping("list")
    public WeiZiResult selectList(RoleParamDTO roleParamDTO) {
        WeiZiPageResult<RoleVO> roleList = roleService.selectList(roleParamDTO);
        return WeiZiResult.success(roleList);
    }

    /**
     * 获取单个详情
     */
    /*@GetMapping("getDetail")
    public WeiZiResult searchAdminById(@RequestParam("adminId") Long adminId) {
        if (ObjectUtil.isNull(adminId)) {
            return WeiZiResult.error("adminId不可为空！");
        }
        UmsAdminVO admin = roleService.searchAdminById(adminId);
        if (ObjectUtil.isNotNull(admin))
            return WeiZiResult.success(admin);
        return WeiZiResult.error("该管理员不存在！");
    }

    *//**
     * 保存菜单
     *//*
    @PostMapping("save")
    public WeiZiResult save(UmsAdminEntity umsAdminEntity) {
        if (ObjectUtil.isNotEmpty(umsAdminEntity))
            return roleService.saveAdmin(umsAdminEntity);
        return WeiZiResult.error("菜单不可为空");
    }

    *//**
     * 更新菜单
     *//*
    @PostMapping("update")
    public WeiZiResult update(UmsAdminDTO umsAdminEntity) {
        if (ObjectUtil.isNotEmpty(umsAdminEntity))
            return roleService.updateAdmin(umsAdminEntity);
        return WeiZiResult.error("菜单不可为空");
    }*/

    /**
     * 删除菜单（二合一删除单个和多个都可以）
     */
    @GetMapping("delete")
    public WeiZiResult delete(@RequestParam("roleId") Long roleId) {
        if (ObjectUtil.isNotEmpty(roleId)) {
            return roleService.deleteByRoleId(roleId);
        }
        return WeiZiResult.error("ID不可为空");
    }
}
