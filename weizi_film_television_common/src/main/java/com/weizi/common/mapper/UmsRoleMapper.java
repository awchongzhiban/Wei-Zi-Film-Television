package com.weizi.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weizi.common.domain.entity.UmsRoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UmsRoleMapper extends BaseMapper<UmsRoleEntity> {
    List<Long> selectByAdminId(@Param("adminId") Long adminId);
}
