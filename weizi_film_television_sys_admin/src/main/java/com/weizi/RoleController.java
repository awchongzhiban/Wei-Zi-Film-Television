package com.weizi;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.domain.dto.RoleDTO;
import com.weizi.common.domain.dto.pageParam.RoleParamDTO;
import com.weizi.common.domain.vo.list.RoleVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.RoleService;
import org.springframework.web.bind.annotation.*;

/**
 * @date AWei
 * @date 2024/02/26
*/
@RestController
@RequestMapping("admin/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
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
    @GetMapping("getDetail")
    public WeiZiResult searchRoleById(@RequestParam("roleId") Long roleId) {
        if (ObjectUtil.isNull(roleId)) {
            return WeiZiResult.error("roleId不可为空！");
        }
        RoleDTO role = roleService.searchRoleById(roleId);
        if (ObjectUtil.isNotNull(role))
            return WeiZiResult.success(role);
        return WeiZiResult.error("该管理员不存在！");
    }

    /**
     * 保存菜单
     */
    @PostMapping("save")
    public WeiZiResult save(@RequestBody RoleDTO roleDTO) {
        if (ObjectUtil.isNotEmpty(roleDTO))
            return roleService.saveRole(roleDTO);
        return WeiZiResult.error("菜单不可为空");
    }

    /**
     * 更新菜单
     */
    @PostMapping("update")
    public WeiZiResult update(@RequestBody RoleDTO roleDTO) {
        if (ObjectUtil.isNotEmpty(roleDTO))
            return roleService.updateRole(roleDTO);
        return WeiZiResult.error("菜单不可为空");
    }

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
