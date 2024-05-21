package com.weizi;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.FileConstants;
import com.weizi.common.domain.dto.AdminDTO;
import com.weizi.common.domain.po.AdminPO;
import com.weizi.common.domain.vo.list.AdminVO;
import com.weizi.common.domain.dto.pageParam.AdminParamDTO;
import com.weizi.common.domain.vo.list.RoleTagVO;
import com.weizi.common.mapper.AdminMapper;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.AdminService;
import com.weizi.common.utils.imageutils.ImageUtils;
import com.weizi.common.utils.redis.AdminTreeService;
import com.weizi.common.utils.security.WeiZiSecurityUtil;
import jakarta.validation.Valid;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

/**
 * @date AWei
 * @date 2024/02/24
 */
@RestController
@RequestMapping("admin/admin")
public class AdminController {
    @Value("${file-upload.avatar.max-size}")
    private String maxAvatarSize;

    @Value("${file-upload.avatar.image-types}")
    private List<String> supportedAvatarTypes;

    @Value("${item.avatar-path}")
    private String avatarPath;

    @Autowired
    private ImageUtils imageUtils;

    private final AdminService adminService;
    private final AdminMapper adminMapper;
    private final AdminTreeService adminTreeService;

    public AdminController(AdminService adminService, AdminMapper adminMapper, AdminTreeService adminTreeService) {
        this.adminService = adminService;
        this.adminMapper = adminMapper;
        this.adminTreeService = adminTreeService;
    }

    @GetMapping("/info")
    public WeiZiResult searchAdminInfo() {
        return WeiZiResult.success(adminService.searchAdminInfo());
    }

    /**
     * 获取管理员列表
     */
    @GetMapping("list")
    public WeiZiResult selectList(AdminParamDTO adminParamDto) {
        WeiZiPageResult<AdminVO> adminList = adminService.selectList(adminParamDto);
        return WeiZiResult.success(adminList);
    }

    /**
     * 获取当前管理员所拥有的角色列表
     */
    @PostMapping("getRoleTagList")
    public WeiZiResult getRoleTagList() {
        List<RoleTagVO> roleTagList = adminService.getRoleTagList();
        return WeiZiResult.success(roleTagList);
    }

    /**
     * 获取单个详情
     */
    @GetMapping("getDetail")
    public WeiZiResult searchAdminById(@RequestParam("adminId") Long adminId) {
        if (ObjectUtil.isNull(adminId)) {
            return WeiZiResult.error("adminId不可为空！");
        }
        AdminVO admin = adminService.searchAdminById(adminId);
        if (ObjectUtil.isNotNull(admin))
            return WeiZiResult.success(admin);
        return WeiZiResult.error("该管理员不存在！");
    }

    /**
     * 保存管理员
     */
    @PostMapping("save")
    public WeiZiResult save(@Valid @RequestBody AdminDTO adminDTO) {
        if (ObjectUtil.isNotEmpty(adminDTO))
            return adminService.saveAdmin(adminDTO);
        return WeiZiResult.error("数据不可为空");
    }

    /**
     * 更新管理员
     */
    @PostMapping("update")
    public WeiZiResult update(@RequestBody AdminDTO adminDTO) {
        adminDTO.setAvatar(null);
        if (ObjectUtil.isNotEmpty(adminDTO)) {
            if (adminTreeService.isInMyBranch(WeiZiSecurityUtil.getAdminId(), adminDTO.getAdminId()))
                return adminService.updateAdmin(adminDTO);
            else
                return WeiZiResult.error("暂无更新该管理员的权限");
        }
        return WeiZiResult.error("数据不可为空");
    }

    /**
     * 删除管理员
     */
    @GetMapping("delete")
    public WeiZiResult delete(@RequestParam("adminId") Long adminId) {
        if (ObjectUtil.isNotEmpty(adminId)) {
            if (!adminTreeService.isInMyBranch(WeiZiSecurityUtil.getAdminId(), adminId))
                return WeiZiResult.error("暂无删除该管理员的权限");
            if (ObjectUtil.isNotNull(WeiZiSecurityUtil.getLoginAdmin().getId()) && !WeiZiSecurityUtil.getLoginAdmin().getId().equals(adminId)) {
                // 先获取原本的头像数据
                AdminVO admin = adminService.searchAdminById(adminId);
                if (ObjectUtil.isNotNull(admin) && ObjectUtil.isNotNull(admin.getAvatar()))
                    imageUtils.deleteImage(admin.getAvatar(), FileConstants.ADMIN_AVATAR);
                return adminService.deleteByAdminId(adminId);
            }
            else
                return WeiZiResult.error("不可删除自身");
        }
        return WeiZiResult.error("ID不可为空");
    }

    @PostMapping("uploadAvatar")
    public WeiZiResult uploadAvatar(@RequestParam("file") MultipartFile file, @RequestParam("adminId") Long adminId) throws IOException {
        if (file.isEmpty()) {
            return WeiZiResult.error("上传文件为空");
        }

        // 获取上传文件的大小
        long fileSize = file.getSize();
        // 获取上传文件的类型
        String fileType = file.getContentType();

        // 判断文件大小是否符合要求
        long maxSize = parseSize(maxAvatarSize);
        if (fileSize > maxSize) {
            return WeiZiResult.error("文件大小超过限制（" + maxAvatarSize + "）");
        }

        // 判断文件类型是否符合要求
        if (!supportedAvatarTypes.contains(fileType) || ObjectUtil.isNull(fileType)) {
            return WeiZiResult.error("文件类型不支持");
        }
        String imageFileName = imageUtils.uploadImage(file, FileConstants.ADMIN_AVATAR);
        // 判断文件名是否为空
        if (ObjectUtil.isNotNull(imageFileName)) {
            // 先获取原本的头像数据
            AdminPO admin = adminMapper.selectById(adminId);
            // 更新成功后删除原本的头像文件，防止冗余
            if (adminService.updateAdminAvatar(imageFileName, adminId)) {
                if (ObjectUtil.isNotNull(admin) && ObjectUtil.isNotNull(admin.getAvatar()))
                    imageUtils.deleteImage(admin.getAvatar(), FileConstants.ADMIN_AVATAR);
                // 获取头像文件路径
                String avatarFilePath = avatarPath + imageFileName;
                String extension = FilenameUtils.getExtension(avatarFilePath);
                // 将头像文件转换为 Base64 编码的字符串并设置到实体对象中
                return WeiZiResult.success("上传成功", "data:image/" + extension.toLowerCase() + ";base64," + imageUtils.encodeImageToBase64(avatarFilePath));
            }
        }
        return WeiZiResult.error("文件上传失败");
    }

    // FIXME 后续加入上传电影或海报等就修改成公共方法
    private long parseSize(String sizeStr) {
        String numberPart = sizeStr.substring(0, sizeStr.length() - 2);
        String unitPart = sizeStr.substring(sizeStr.length() - 2).toUpperCase();
        long multiplier = switch (unitPart) {
            case "KB" -> 1024;
            case "MB" -> 1024 * 1024;
            case "GB" -> 1024 * 1024 * 1024;
            default -> throw new IllegalArgumentException("Invalid size unit: " + unitPart);
        };
        return Long.parseLong(numberPart) * multiplier;
    }
}
