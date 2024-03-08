package com.weizi.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weizi.common.domain.entity.UmsRoleEntity;
import com.weizi.common.domain.vo.list.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UmsRoleMapper extends BaseMapper<UmsRoleEntity> {
    List<Long> selectByAdminId(@Param("adminId") Long adminId);

    List<RoleVO> selectRolePage(@Param("roleLabel")String roleLabel, @Param("roleName") String roleName, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);

    int countTotal(@Param("roleLabel")String roleLabel, @Param("roleName") String roleName);
}
