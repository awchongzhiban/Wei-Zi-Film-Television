package com.weizi.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weizi.common.domain.dto.RoleDTO;
import com.weizi.common.domain.po.RoleTreeNode;
import com.weizi.common.domain.vo.list.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<RoleDTO> {
    // 通过管理员ID查询中间表获取角色ID
    List<Long> selectByAdminId(@Param("adminId") Long adminId);
    // 角色分页总数
    int countTotal(@Param("roleIds") List<Long> roleIds, @Param("roleLabel")String roleLabel, @Param("roleName") String roleName, @Param("superAdminRoleId") Long superAdminRoleId);
    // 角色分页
    List<RoleVO> selectRolePage(@Param("roleIds") List<Long> roleIds, @Param("roleLabel")String roleLabel, @Param("roleName") String roleName, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize, @Param("superAdminRoleId") Long superAdminRoleId);
    // 获取所有角色ID和父ID生成树用于放入redis中
    List<RoleTreeNode> findAllRoleIdAndParentId();
    // 删除角色ID和菜单ID中间表
    int deleteRoleToMenuIdList(@Param("roleId") Long roleId);
    // 角色ID和菜单ID列表插入中间表
    int insertRoleToMenuIdList(@Param("roleId") Long roleId, @Param("menuIdList") List<Long> menuIdList);
    // 更新角色
    int updateRole(RoleDTO roleDTO);

    RoleDTO searchRoleById(@Param("roleId") Long roleId);
}
