package com.weizi.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.dto.UmsAdminParamDto;
import com.weizi.common.domain.entity.UmsAdminEntity;
import com.weizi.common.domain.entity.UmsMenuEntity;
import com.weizi.common.domain.vo.AdminInfoVO;
import com.weizi.common.exception.ServiceException;
import com.weizi.common.mapper.UmsAdminMapper;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.IUmsAdminService;
import com.weizi.common.utils.security.WeiZiSecurityUtil;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper, UmsAdminEntity> implements IUmsAdminService {

    private final UmsAdminMapper adminMapper;

    public UmsAdminServiceImpl(UmsAdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }
    @Override
    public AdminInfoVO searchAdminInfo() {
        // 获取用户id
        Long adminId = WeiZiSecurityUtil.getAdminId();
        UmsAdminEntity umsAdmin = adminMapper.selectById(adminId);
        if(ObjectUtil.isNull(umsAdmin)) {
            throw new ServiceException(HttpStatus.FORBIDDEN,"");
        }

        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtil.copyProperties(umsAdmin,adminInfoVO);
        return adminInfoVO;
    }

    @Override
    public WeiZiPageResult<UmsAdminEntity> selectList(UmsAdminParamDto adminParamDto) {
        // 计算分页查询的偏移量
        Long pageNum = (adminParamDto.getPageNum() - 1) * adminParamDto.getPageSize();
        // 执行分页查询
        List<UmsAdminEntity> records = baseMapper.selectAdminPage(adminParamDto.getUsername(), adminParamDto.getNickname(), adminParamDto.getEmail(), adminParamDto.getMobile(), pageNum, adminParamDto.getPageSize());
        // 查询总记录数
        int total = baseMapper.countTotal(adminParamDto.getUsername(), adminParamDto.getNickname(), adminParamDto.getEmail(), adminParamDto.getMobile());
        return new WeiZiPageResult<>(records, total);
    }

    @Override
    public UmsAdminEntity searchAdminById(Long adminId) {
        return baseMapper.searchAdminById(adminId);
    }

    @Override
    public WeiZiResult saveAdmin(UmsAdminEntity umsAdminEntity) {
        return null;
    }

    @Override
    public WeiZiResult updateAdmin(UmsAdminEntity umsAdminEntity) {
        return null;
    }

    @Override
    public WeiZiResult deleteByAdminId(Long adminId) {
        return null;
    }

    @Override
    public boolean updateAdminAvatar(String imageFileName, Long adminId) {
        return baseMapper.updateAdminAvatar(imageFileName, adminId) >= 0;
    }
}
