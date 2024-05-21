package com.weizi.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.dto.pageParam.MenuParamDTO;
import com.weizi.common.domain.po.MenuPO;
import com.weizi.common.domain.vo.RouterVO;
import com.weizi.common.mapper.MenuMapper;
import com.weizi.common.mapper.RoleMapper;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.MenuService;
import com.weizi.common.utils.security.WeiZiSecurityUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuPO> implements MenuService {

    private final RoleMapper roleMapper;

    public MenuServiceImpl(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /**
     * 获取个人菜单列表
     */
    @Override
    public List<RouterVO> searchSelfMenu() {
        Long adminId = WeiZiSecurityUtil.getAdminId();
        List<Long> roleIds = roleMapper.selectByAdminId(adminId);
        List<MenuPO> menuList = baseMapper.selectAdminByRoleIds(roleIds);
        // 通过递归设置菜单的树形结构
        // 1、获取所有的1级菜单【parentId = 0】
        // 2、遍历1级菜单，获取他所有的子元素【其他数据的parentId = 当前元素的menuId】
        return _getRouter(menuList);
    }

    @Override
    public WeiZiPageResult<MenuPO> selectList(MenuParamDTO menuParamDto) {
        // 这里是从数据库获取数据
        WeiZiPageResult<MenuPO> parentMenuList = _selectPage(menuParamDto);
        List<MenuPO> record = parentMenuList.getList();
        // 这里递归把子数据放入到对应的父级数据中
        List<MenuPO> menusList = _buildMenuChildren(record);
        // 最后重新设置数组，返回的数据其实时有两个数据  total：总数据量，data：数据
        parentMenuList.setList(menusList);
        return parentMenuList;
    }

    @Override
    public MenuPO searchMenuById(Long menuId) {
        return baseMapper.selectByMenuId(menuId);
    }

    @Override
    public WeiZiResult saveMenu(MenuPO menuPO) {
        // 判断是否存在
        if (ObjectUtil.isNull(menuPO.getParentId())) menuPO.setParentId(0L);
        int count = baseMapper.checkCount(menuPO.getParentId(), menuPO.getMenuName(), menuPO.getPath(), menuPO.getPerms(), null);
        if (count > 0) {
            return WeiZiResult.error(HttpStatus.ERROR_MESSAGE, "该菜单已存在，或路径已存在！");
        }
        baseMapper.insert(menuPO);
        return WeiZiResult.success();
    }

    @Override
    public WeiZiResult updateMenu(MenuPO menuPO) {
        // 判断是否存在
        MenuPO existingMenu = baseMapper.selectByMenuId(menuPO.getMenuId());
        if (existingMenu == null) {
            return WeiZiResult.error("要更新的菜单不存在！");
        }
        if (ObjectUtil.isNull(menuPO.getParentId())) menuPO.setParentId(0L);
        int count = baseMapper.checkCount(menuPO.getParentId(), menuPO.getMenuName(), menuPO.getPath(), menuPO.getPerms(), existingMenu.getMenuId());
        if (count > 0) {
            return WeiZiResult.error("该菜单已存在，或路径已存在！");
        }
        baseMapper.updateById(menuPO);
        return WeiZiResult.success();
    }

    @Override
    public WeiZiResult deleteMenusByMenuId(List<Long> menuIds) {
        if (ObjectUtil.isEmpty(menuIds)) {
            return WeiZiResult.error("菜单ID列表为空");
        }

        // 获取所有子菜单ID
        List<Long> allMenuIds = baseMapper.getChildrenMenuIds(menuIds);
        allMenuIds.addAll(menuIds);

        // 删除关联表中的记录和菜单表中的子菜单
        int deletedRoleToMenuCount = baseMapper.deleteRoleToMenuByMenuIds(allMenuIds);
        int deletedMenuCount = baseMapper.deleteMenusByMenuIds(allMenuIds);
        if (deletedRoleToMenuCount >= 0 && deletedMenuCount >= 0)
            return WeiZiResult.success();
        return WeiZiResult.error("删除失败");
    }


    /**
     * 查看所有菜单
     */
    @Override
    public List<RouterVO> selectMenuList() {
        List<MenuPO> menuList = baseMapper.selectList(null);
        // 渲染children
        return _getRouter(menuList);
    }

    /**
     * 获取路由
     */
    private List<RouterVO> _getRouter(List<MenuPO> menuList) {
        List<RouterVO> routerVOS = new ArrayList<>();
        // 首先获取所有的1级路由
        List<MenuPO> parentMenu = menuList.stream().filter(item -> item.getParentId() == 0).toList();
        // 转换对象类型
        for (MenuPO menu : parentMenu) {
            RouterVO routerVO = new RouterVO();
            BeanUtil.copyProperties(menu, routerVO);
            routerVOS.add(routerVO);
        }
        // 循环1级路由，获取所有的子菜单
        for (RouterVO routerVO : routerVOS) {
            // 获取所有的子节点
            List<RouterVO> childrenList = _buildTree(menuList, routerVO.getMenuId());
            routerVO.setChildren(childrenList);
        }
        return routerVOS;
    }

    /**
     * 获取所有子节点，递归获取【如果是2级不需要递归了】
     */
    private List<RouterVO> _buildTree(List<MenuPO> allMenu, Long parentId) {
        List<RouterVO> childrenList = new ArrayList<>();
        // 遍历所有的数据
        for (MenuPO menu : allMenu) {
            // 判断menu的parentId是否与传进来的parentId相同
            if (menu.getParentId().equals(parentId)) {
                RouterVO routerVO = new RouterVO();
                BeanUtil.copyProperties(menu, routerVO);
                childrenList.add(routerVO);
            }
        }
        // 递归childrenList可能还有子节点
        for (RouterVO childrenItem : childrenList) {
            childrenItem.setChildren(_buildTree(allMenu, childrenItem.getMenuId()));
        }
        return childrenList;
    }

    /**
     * 构建子菜单
     */
    private List<MenuPO> _buildMenuChildren(List<MenuPO> menuList) {
        // 首先获取所有的1级路由
        List<MenuPO> parentMenu = menuList.stream().filter(item -> item.getParentId() == 0).collect(Collectors.toList());
        // 循环1级路由，获取所有的子菜单
        for (MenuPO menuEntity : parentMenu) {
            // 获取所有的子节点
            List<MenuPO> childrenList = _buildMenuTree(menuList, menuEntity.getMenuId());
            menuEntity.setChildren(childrenList);
        }
        return parentMenu;
    }

    /**
     * 获取所有子节点，递归获取【如果是2级不需要递归了】
     */
    private List<MenuPO> _buildMenuTree(List<MenuPO> allMenu, Long parentId) {
        List<MenuPO> childrenList = new ArrayList<>();
        // 遍历所有的数据
        for (MenuPO menu : allMenu) {
            // 判断menu的parentId是否与传进来的parentId相同
            if (menu.getParentId().equals(parentId)) {
                childrenList.add(menu);
            }
        }
        // 递归childrenList可能还有子节点
        for (MenuPO childrenItem : childrenList) {
            childrenItem.setChildren(_buildMenuTree(allMenu, childrenItem.getMenuId()));
        }
        return childrenList;
    }

    private WeiZiPageResult<MenuPO> _selectPage(MenuParamDTO menuParamDto) {
        // 计算分页查询的偏移量
        Long pageNum = (menuParamDto.getPageNum() - 1) * menuParamDto.getPageSize();
        // 执行分页查询
        List<MenuPO> records = baseMapper.selectMenuPage(menuParamDto.getMenuName(), menuParamDto.getPerms(), pageNum, menuParamDto.getPageSize());

        // 获取到最外层父级Id数组
        List<Long> menuIdList = records.stream().map(MenuPO::getMenuId).toList();
        if (ObjectUtil.isNotEmpty(menuIdList)) {
            // 获取所有子菜单数组
            List<MenuPO> childrenMenus = baseMapper.getChildrenMenu(menuIdList);
            // 判空后加入到records内
            if (ObjectUtil.isNotEmpty(childrenMenus))
                records.addAll(childrenMenus);
        }
        // 查询总记录数
        int total = baseMapper.countTotal(menuParamDto.getMenuName(), menuParamDto.getPerms());
        return new WeiZiPageResult<>(records, total);
    }

    // 递归获取当前菜单及其所有子菜单的 ID
    private void _recursiveGetMenuIdsToDelete(Long menuId, Long[] menuIds, Set<Long> menuIdsToDelete) {
        // 如果不在当前菜单的父菜单 ID 在传入的 menuIds 中，则将当前菜单添加到待删除集合中
        if (!ArrayUtil.contains(menuIds, menuId)) {
            menuIdsToDelete.add(menuId);
        }
        // 查询当前菜单的所有子菜单
        List<Long> childMenus = baseMapper.getChildMenusByParentId(menuId);
        // 递归处理子菜单
        for (Long childMenuId : childMenus) {
            _recursiveGetMenuIdsToDelete(childMenuId, menuIds, menuIdsToDelete);
        }
    }
}
