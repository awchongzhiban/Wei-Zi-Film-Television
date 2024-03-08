package com.weizi.common.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.domain.dto.pageParam.RoleParamDTO;
import com.weizi.common.domain.entity.UmsRoleEntity;
import com.weizi.common.domain.vo.list.AdminVO;
import com.weizi.common.domain.vo.list.RoleVO;
import com.weizi.common.mapper.UmsRoleMapper;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.IUmsRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmsRoleServiceImpl extends ServiceImpl<UmsRoleMapper, UmsRoleEntity> implements IUmsRoleService {
    @Override
    public WeiZiPageResult<RoleVO> selectList(RoleParamDTO roleParamDto) {
        // 计算分页查询的偏移量
        Long pageNum = (roleParamDto.getPageNum() - 1) * roleParamDto.getPageSize();
        // 执行分页查询
        List<RoleVO> records = baseMapper.selectRolePage(roleParamDto.getRoleLabel(), roleParamDto.getRoleName(), pageNum, roleParamDto.getPageSize());
        // 查询总记录数
        int total = baseMapper.countTotal(roleParamDto.getRoleLabel(), roleParamDto.getRoleName());
        return new WeiZiPageResult<>(records, total);
    }

    @Override
    public UmsRoleEntity searchRoleById(Long adminId) {
        return null;
    }

    @Override
    public WeiZiResult saveRole(UmsRoleEntity umsRoleEntity) {
        return null;
    }

    @Override
    public WeiZiResult updateRole(UmsRoleEntity umsRoleEntity) {
        return null;
    }

    @Override
    public WeiZiResult deleteByRoleId(Long adminId) {
        return null;
    }
}
