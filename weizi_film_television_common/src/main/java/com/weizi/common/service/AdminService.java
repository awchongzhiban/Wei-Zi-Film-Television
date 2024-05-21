package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.AdminDTO;
import com.weizi.common.domain.po.AdminPO;
import com.weizi.common.domain.vo.list.AdminVO;
import com.weizi.common.domain.dto.pageParam.AdminParamDTO;
import com.weizi.common.domain.vo.AdminInfoVO;
import com.weizi.common.domain.vo.list.RoleTagVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;

import java.util.List;

public interface AdminService extends IService<AdminPO> {
    AdminInfoVO searchAdminInfo();

    WeiZiPageResult<AdminVO> selectList(AdminParamDTO adminParamDto);

    AdminVO searchAdminById(Long adminId);

    WeiZiResult saveAdmin(AdminDTO adminDTO);

    WeiZiResult updateAdmin(AdminDTO adminDTO);

    WeiZiResult deleteByAdminId(Long adminId);

    boolean updateAdminAvatar(String imageFileName, Long adminId);

    List<RoleTagVO> getRoleTagList();
}
