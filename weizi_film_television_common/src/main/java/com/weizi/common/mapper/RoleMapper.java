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
    List<Long> selectByAdminId(@Param("adminId") Long adminId);

    List<RoleVO> selectRolePage(@Param("roleIds") List<Long> roleIds, @Param("roleLabel")String roleLabel, @Param("roleName") String roleName, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);

    int countTotal(@Param("roleIds") List<Long> roleIds, @Param("roleLabel")String roleLabel, @Param("roleName") String roleName);

    List<RoleTreeNode> findAllRoleIdAndParentId();

    int insertRoleToMenuIdList(@Param("roleId") Long roleId, @Param("menuIdList") List<Long> menuIdList);

    int updateRole(RoleDTO roleDTO);

    int deleteRoleToMenuIdList(@Param("roleId") Long roleId);

    RoleDTO searchRoleById(@Param("roleId") Long roleId);
}
