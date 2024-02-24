package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.UmsAdminParamDto;
import com.weizi.common.domain.entity.UmsAdminEntity;
import com.weizi.common.domain.vo.AdminInfoVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;

public interface IUmsAdminService extends IService<UmsAdminEntity> {
    AdminInfoVO searchAdminInfo();

    WeiZiPageResult<UmsAdminEntity> selectList(UmsAdminParamDto adminParamDto);

    UmsAdminEntity searchAdminById(Long adminId);

    WeiZiResult saveAdmin(UmsAdminEntity umsAdminEntity);

    WeiZiResult updateAdmin(UmsAdminEntity umsAdminEntity);

    WeiZiResult deleteByAdminId(Long adminId);

    boolean updateAdminAvatar(String imageFileName, Long adminId);
}
