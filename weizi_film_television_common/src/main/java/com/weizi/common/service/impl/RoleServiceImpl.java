package com.weizi.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.domain.dto.RoleDTO;
import com.weizi.common.domain.dto.pageParam.RoleParamDTO;
import com.weizi.common.domain.vo.list.RoleVO;
import com.weizi.common.mapper.RoleMapper;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.RoleService;
import com.weizi.common.utils.redis.RoleTreeService;
import com.weizi.common.utils.security.WeiZiSecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleDTO> implements RoleService {

    private final RoleTreeService roleTreeService;

    public RoleServiceImpl(RoleTreeService roleTreeService) {
        this.roleTreeService = roleTreeService;
    }

    @Override
    public WeiZiPageResult<RoleVO> selectList(RoleParamDTO roleParamDto) {
        // 计算分页查询的偏移量
        Long pageNum = (roleParamDto.getPageNum() - 1) * roleParamDto.getPageSize();
        // 执行分页查询
        List<RoleVO> records = baseMapper.selectRolePage(WeiZiSecurityUtil.getRoleIdList(), roleParamDto.getRoleLabel(), roleParamDto.getRoleName(), pageNum, roleParamDto.getPageSize());
        // 查询总记录数
        int total = baseMapper.countTotal(WeiZiSecurityUtil.getRoleIdList(), roleParamDto.getRoleLabel(), roleParamDto.getRoleName());
        return new WeiZiPageResult<>(records, total);
    }

    @Override
    public WeiZiResult saveRole(RoleDTO roleDTO) {
        roleDTO.setParentRoleId(WeiZiSecurityUtil.getAdminId());
        if (baseMapper.insert(roleDTO) == 1) {
            baseMapper.insertRoleToMenuIdList(roleDTO.getRoleId(), roleDTO.getMenuIdList());
            return WeiZiResult.success();
        }
        return WeiZiResult.error("没有角色权限");
    }

    @Override
    public WeiZiResult updateRole(RoleDTO roleDTO) {
        if (roleTreeService.isInMyBranch(roleDTO.getParentRoleId())) {
            if (baseMapper.updateRole(roleDTO) > 0) {
                baseMapper.deleteRoleToMenuIdList(roleDTO.getRoleId());
                if (roleDTO.getMenuIdList().isEmpty()) return WeiZiResult.success();
                baseMapper.insertRoleToMenuIdList(roleDTO.getRoleId(), roleDTO.getMenuIdList());
                return WeiZiResult.success();
            }
            return WeiZiResult.error("更新失败");
        }
        return WeiZiResult.error("没有角色权限");
    }

    @Override
    public WeiZiResult deleteByRoleId(Long adminId) {
        return null;
    }

    @Override
    public RoleDTO searchRoleById(Long roleId) {
        return baseMapper.searchRoleById(roleId);
    }
}
