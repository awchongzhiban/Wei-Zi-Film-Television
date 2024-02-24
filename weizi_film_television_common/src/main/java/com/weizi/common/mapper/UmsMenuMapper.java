package com.weizi.common.mapper;

import com.weizi.common.domain.entity.UmsMenuEntity;
import com.weizi.common.response.WeiZiResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UmsMenuMapper extends BaseMapperX<UmsMenuEntity> {
    // 获取管理员角色id组
    List<UmsMenuEntity> selectAdminByRoleIds(@Param("roleIds") List<Long> roleIds);
    // 通过父级id组获取菜单
    List<UmsMenuEntity> selectMenusByParentIds(@Param("parentIds") List<Long> parentIds);
    // 获取菜单分页
    List<UmsMenuEntity> selectMenuPage(@Param("menuName") String menuName, @Param("perms") String perms,@Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);
    // 获取父级菜单总数
    int countTotal(@Param("menuName") String menuName,@Param("perms")  String perms);
    // 通过菜单id获取信息
    UmsMenuEntity selectByMenuId(@Param("menuId") Long menuId);
    // 用于新增的时候确保在当前父级id下没有重命名
    int checkCount(@Param("parentId") Long parentId, @Param("menuName") String menuName, @Param("path") String path, @Param("perms") String perms, @Param("menuId") Long menuId);
    // 获取子菜单数据
    List<UmsMenuEntity> getChildrenMenu(@Param("parentIds") List<Long> parentIds);

    List<Long> getChildrenMenuIds(@Param("menuIds") List<Long> menuIds);

    int deleteMenusByMenuIds(@Param("menuIds") List<Long> menuIds);

    int deleteRoleToMenuByMenuIds(@Param("menuIds") List<Long> menuIds);

    List<Long> getChildMenusByParentId(@Param("menuId") Long menuId);
}
