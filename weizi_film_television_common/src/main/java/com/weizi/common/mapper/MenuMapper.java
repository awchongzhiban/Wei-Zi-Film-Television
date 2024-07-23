package com.weizi.common.mapper;

import com.weizi.common.domain.po.MenuPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapperX<MenuPO> {
    // 获取管理员角色id组
    List<MenuPO> selectAdminByRoleIds(@Param("roleIds") List<Long> roleIds);
    // 通过父级id组获取菜单
    List<MenuPO> selectMenusByParentIds(@Param("parentIds") List<Long> parentIds);
    // 超级管理员直接获取菜单
    List<MenuPO> selectAllMenu();
    // 获取菜单分页
    List<MenuPO> selectMenuPage(@Param("menuName") String menuName, @Param("perms") String perms, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);
    // 获取父级菜单总数
    int countTotal(@Param("menuName") String menuName,@Param("perms")  String perms);
    // 通过菜单id获取信息
    MenuPO selectByMenuId(@Param("menuId") Long menuId);
    // 用于新增的时候确保在当前父级id下没有重命名
    int checkCount(@Param("parentId") Long parentId, @Param("menuName") String menuName, @Param("path") String path, @Param("perms") String perms, @Param("menuId") Long menuId);
    // 获取子菜单数据
    List<MenuPO> getChildrenMenu(@Param("parentIds") List<Long> parentIds);

    List<Long> getChildrenMenuIds(@Param("menuId") Long menuId);

    int deleteMenusByMenuIds(@Param("menuIds") List<Long> menuIds);

    int deleteRoleToMenuByMenuIds(@Param("menuIds") List<Long> menuIds);

    List<Long> getChildMenusByParentId(@Param("menuId") Long menuId);
}
