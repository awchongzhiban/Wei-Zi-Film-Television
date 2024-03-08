package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.pageParam.RoleParamDTO;
import com.weizi.common.domain.entity.UmsRoleEntity;
import com.weizi.common.domain.vo.list.RoleVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;

public interface IUmsRoleService extends IService<UmsRoleEntity> {
    WeiZiPageResult<RoleVO> selectList(RoleParamDTO adminParamDto);

    UmsRoleEntity searchRoleById(Long adminId);

    WeiZiResult saveRole(UmsRoleEntity umsRoleEntity);

    WeiZiResult updateRole(UmsRoleEntity umsRoleEntity);

    WeiZiResult deleteByRoleId(Long adminId);
}
