package com.weizi;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.domain.dto.pageParam.MenuParamDTO;
import com.weizi.common.domain.po.MenuPO;
import com.weizi.common.domain.vo.RouterVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.MenuService;
import com.weizi.common.utils.security.WeiZiSecurityUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
    * 获取用户自身的菜单权限
    */
    @GetMapping("self")
    public WeiZiResult searchSelfMenu() {
        List<RouterVO> menuList = menuService.searchSelfMenu();
        return WeiZiResult.success(menuList);
    }

    /**
     * 获取所有列表，展示弃用
     */
    @GetMapping("allList")
    public WeiZiResult searchMenuList() {
        List<RouterVO> menuList = menuService.selectMenuList();
        return WeiZiResult.success(menuList);
    }

    /**
     * 获取菜单列表
     */
    @GetMapping("list")
    public WeiZiResult selectList(MenuParamDTO menuParamDto) {
        WeiZiPageResult<MenuPO> menuList = menuService.selectList(menuParamDto);
        return WeiZiResult.success(menuList);
    }

    /**
     * 获取单个详情
     */
    @GetMapping("getDetail")
    public WeiZiResult searchMenuById(@RequestParam("menuId") Long menuId) {
        if (ObjectUtil.isNull(menuId)) {
            return WeiZiResult.error("menuId不可为空！");
        }
        MenuPO menu = menuService.searchMenuById(menuId);
        return WeiZiResult.success(menu);
    }

    /**
     * 保存菜单
     */
    @PostMapping("save")
    public WeiZiResult save(@RequestBody MenuPO menuPO) {
        if (ObjectUtil.isNotEmpty(menuPO))
            return menuService.saveMenu(menuPO);
        return WeiZiResult.error("菜单不可为空");
    }

    /**
     * 更新菜单
     */
    @PostMapping("update")
    public WeiZiResult update(@RequestBody MenuPO menuPO) {
        if (ObjectUtil.isNotEmpty(menuPO))
            return menuService.updateMenu(menuPO);
        return WeiZiResult.error("菜单不可为空");
    }

    /**
     * 删除菜单（二合一删除单个和多个都可以）
     */
    @PostMapping("delete")
    public WeiZiResult delete(@RequestBody Long[] menuIds) {
        if (ObjectUtil.isNotEmpty(menuIds)) {
            return menuService.deleteMenusByMenuId(List.of(menuIds));
        }
        return WeiZiResult.error("删除失败");
    }
}
