package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.RoleDTO;
import com.weizi.common.domain.dto.pageParam.RoleParamDTO;
import com.weizi.common.domain.vo.list.RoleVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;

public interface RoleService extends IService<RoleDTO> {
    WeiZiPageResult<RoleVO> selectList(RoleParamDTO adminParamDto);

    WeiZiResult saveRole(RoleDTO roleDTO);

    WeiZiResult updateRole(RoleDTO roleDTO);

    WeiZiResult deleteByRoleId(Long adminId);

    RoleDTO searchRoleById(Long roleId);
}
