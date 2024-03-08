package com.weizi.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.domain.dto.dataParam.AdminDTO;
import com.weizi.common.domain.vo.list.AdminVO;
import com.weizi.common.utils.imageutils.ImageUtils;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.dto.pageParam.AdminParamDTO;
import com.weizi.common.domain.vo.AdminInfoVO;
import com.weizi.common.exception.ServiceException;
import com.weizi.common.mapper.UmsAdminMapper;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.IUmsAdminService;
import com.weizi.common.utils.security.WeiZiSecurityUtil;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UmsAdminServiceImpl extends ServiceImpl<UmsAdminMapper, AdminDTO> implements IUmsAdminService {

    @Value("${item.avatar-path}")
    private String avatarPath;

    private final UmsAdminMapper adminMapper;
    private final ImageUtils imageUtils;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UmsAdminServiceImpl(UmsAdminMapper adminMapper, ImageUtils imageUtils) {
        this.adminMapper = adminMapper;
        this.imageUtils = imageUtils;
    }
    @Override
    public AdminInfoVO searchAdminInfo() {
        // 获取用户id
        Long adminId = WeiZiSecurityUtil.getAdminId();
        AdminDTO umsAdmin = adminMapper.selectById(adminId);
        if(ObjectUtil.isNull(umsAdmin)) {
            throw new ServiceException(HttpStatus.FORBIDDEN,"");
        }

        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtil.copyProperties(umsAdmin,adminInfoVO);
        Optional.ofNullable(adminInfoVO.getAvatar()).ifPresent(avatar -> {
            String extension = FilenameUtils.getExtension(avatar);
            adminInfoVO.setAvatar("data:image/" + extension.toLowerCase() + ";base64," + imageUtils.encodeImageToBase64(avatarPath + avatar));
        });

        return adminInfoVO;
    }

    @Override
    public WeiZiPageResult<AdminVO> selectList(AdminParamDTO adminParamDto) {
        // 计算分页查询的偏移量
        Long pageNum = (adminParamDto.getPageNum() - 1) * adminParamDto.getPageSize();
        // 执行分页查询
        List<AdminVO> records = baseMapper.selectAdminPage(adminParamDto.getUsername(), adminParamDto.getNickname(), adminParamDto.getEmail(), adminParamDto.getMobile(), pageNum, adminParamDto.getPageSize());
        // 对每个管理员实体的头像进行处理
        records.forEach(entity -> {
            if (ObjectUtil.isNull(entity.getAvatar()))
                return;
            // 获取头像文件路径
            String avatarFilePath = avatarPath + entity.getAvatar();
            // 将头像文件转换为 Base64 编码的字符串并设置到实体对象中
            entity.setAvatar(imageUtils.encodeImageToBase64(avatarFilePath));
        });
        // 查询总记录数
        int total = baseMapper.countTotal(adminParamDto.getUsername(), adminParamDto.getNickname(), adminParamDto.getEmail(), adminParamDto.getMobile());
        return new WeiZiPageResult<>(records, total);
    }

    @Override
    public AdminVO searchAdminById(Long adminId) {
        AdminVO adminDTO = baseMapper.searchAdminById(adminId);
        if (ObjectUtil.isNotNull(adminDTO.getAvatar())) {
            // 获取头像文件路径
            String avatarFilePath = avatarPath + adminDTO.getAvatar();
            // 将头像文件转换为 Base64 编码的字符串并设置到实体对象中
            String base64Image = imageUtils.encodeImageToBase64(avatarFilePath);
            // 获取文件后缀名
            String extension = FilenameUtils.getExtension(avatarFilePath);
            // 将头像文件转换为 Base64 编码的字符串并设置到实体对象中
            adminDTO.setAvatar("data:image/" + extension.toLowerCase() + ";base64," + base64Image);
        }
        return adminDTO;
    }

    @Override
    public WeiZiResult saveAdmin(AdminDTO adminDTO) {
        adminDTO.setPassword(encoder.encode(adminDTO.getPassword()));
        if (baseMapper.insert(adminDTO) > 0)
            return WeiZiResult.success();
        return WeiZiResult.error();
    }

    @Override
    public WeiZiResult updateAdmin(AdminDTO adminDTO) {
        adminDTO.setPassword(null);
        if (baseMapper.updateById(adminDTO) > 0)
            return WeiZiResult.success();
        return WeiZiResult.error();
    }

    @Override
    public WeiZiResult deleteByAdminId(Long adminId) {
        if (baseMapper.deleteById(adminId) >= 0)
            return WeiZiResult.success();
        return WeiZiResult.error();
    }

    @Override
    public boolean updateAdminAvatar(String imageFileName, Long adminId) {
        return baseMapper.updateAdminAvatar(imageFileName, adminId) >= 0;
    }
}
