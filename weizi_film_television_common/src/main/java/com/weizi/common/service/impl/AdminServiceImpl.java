package com.weizi.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.domain.dto.AdminDTO;
import com.weizi.common.domain.po.AdminPO;
import com.weizi.common.domain.vo.list.AdminVO;
import com.weizi.common.domain.vo.list.RoleTagVO;
import com.weizi.common.utils.imageutils.ImageUtils;
import com.weizi.common.constants.HttpStatus;
import com.weizi.common.domain.dto.pageParam.AdminParamDTO;
import com.weizi.common.domain.vo.AdminInfoVO;
import com.weizi.common.exception.ServiceException;
import com.weizi.common.mapper.AdminMapper;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.AdminService;
import com.weizi.common.utils.redis.AdminTreeService;
import com.weizi.common.utils.redis.RoleTreeService;
import com.weizi.common.utils.security.WeiZiSecurityUtil;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminPO> implements AdminService {

    private final RoleTreeService roleTreeService;
    @Value("${item.avatar-path}")
    private String avatarPath;

    private final AdminMapper adminMapper;
    private final AdminTreeService adminTreeService;
    private final ImageUtils imageUtils;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminServiceImpl(AdminMapper adminMapper, AdminTreeService adminTreeService, ImageUtils imageUtils, RoleTreeService roleTreeService) {
        this.adminMapper = adminMapper;
        this.adminTreeService = adminTreeService;
        this.imageUtils = imageUtils;
        this.roleTreeService = roleTreeService;
    }
    @Override
    public AdminInfoVO searchAdminInfo() {
        // 获取用户id
        Long adminId = WeiZiSecurityUtil.getAdminId();
        AdminPO adminPO = adminMapper.selectById(adminId);
        if(ObjectUtil.isNull(adminPO)) {
            throw new ServiceException(HttpStatus.FORBIDDEN,"");
        }

        AdminInfoVO adminInfoVO = new AdminInfoVO();
        BeanUtil.copyProperties(adminPO,adminInfoVO);
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
        // 查询总记录数
        int total = baseMapper.countTotal(WeiZiSecurityUtil.getAdminId(), adminParamDto.getUsername(), adminParamDto.getNickname(), adminParamDto.getEmail(), adminParamDto.getMobile());
        // 如果总记录数为 0，则直接返回
        if (total == 0) return new WeiZiPageResult<>(null, 0);
        // 执行分页查询
        List<AdminVO> records = baseMapper.selectAdminPage(WeiZiSecurityUtil.getAdminId(), adminParamDto.getUsername(), adminParamDto.getNickname(), adminParamDto.getEmail(), adminParamDto.getMobile(), pageNum, adminParamDto.getPageSize());
        // 对每个管理员实体的头像进行处理
        records.forEach(entity -> {
            if (ObjectUtil.isNull(entity.getAvatar()))
                return;
            // 获取头像文件路径
            String avatarFilePath = avatarPath + entity.getAvatar();
            // 将头像文件转换为 Base64 编码的字符串并设置到实体对象中
            entity.setAvatar(imageUtils.encodeImageToBase64(avatarFilePath));
        });
        return new WeiZiPageResult<>(records, total);
    }

    @Override
    public AdminDTO searchAdminById(Long adminId) {
        AdminDTO adminDTO = baseMapper.searchAdminById(adminId);
        System.out.println("adminDTO: "+adminDTO);
        if (ObjectUtil.isNotNull(adminDTO.getAvatar())) {
            // 获取头像文件路径
            String avatarFilePath = avatarPath + adminDTO.getAvatar();
            // 将头像文件转换为 Base64 编码的字符串并设置到实体对象中
            String base64Image = imageUtils.encodeImageToBase64(avatarFilePath);
            // 如果不存在为null就直接返回
            if (ObjectUtil.isNull(base64Image)) return adminDTO;
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
        AdminPO adminPO = new AdminPO();
        BeanUtil.copyProperties(adminDTO, adminPO);
        if (baseMapper.insert(adminPO) > 0) {
            adminTreeService.addAdmin(adminPO.getParentAdminId(), adminPO.getAdminId());
            return WeiZiResult.success();
        }
        return WeiZiResult.error();
    }

    @Override
    public WeiZiResult updateAdmin(AdminDTO adminDTO) {
        adminDTO.setPassword(null);
        AdminPO adminPO = new AdminPO();
        BeanUtil.copyProperties(adminDTO, adminPO);
        if (baseMapper.updateById(adminPO) > 0 && baseMapper.deleteAdminToRoleIdList(adminDTO.getAdminId()) >= 0) {
            if (ObjectUtil.isNotEmpty(adminDTO.getRoleIdList()))
                baseMapper.insertAdminToRoleIdList(adminDTO.getAdminId(), adminDTO.getRoleIdList());
            // 有修改的话直接更新树就好了
            roleTreeService.refreshRoleTree();
            return WeiZiResult.success();
        }
        return WeiZiResult.error();
    }

    @Override
    public String searchAdminAvatarById(Long adminId) {
        return baseMapper.searchAdminAvatarById(adminId);
    }

    @Override
    public WeiZiResult deleteByAdminId(Long adminId) {
        if (baseMapper.deleteById(adminId) >= 0) {
            adminTreeService.deleteAdmin(adminId);
            return WeiZiResult.success();
        }
        return WeiZiResult.error();
    }

    @Override
    public boolean updateAdminAvatar(String imageFileName, Long adminId) {
        return baseMapper.updateAdminAvatar(imageFileName, adminId) >= 0;
    }

    @Override
    public List<RoleTagVO> getRoleTagList() {
        if (WeiZiSecurityUtil.isSuperAdmin()) return baseMapper.getAllRoleTagList();
        return baseMapper.getRoleTagList(WeiZiSecurityUtil.getRoleIdList());
    }

    @Override
    public AdminPO selectById(Long adminId) {
        return baseMapper.selectById(adminId);
    }
}
