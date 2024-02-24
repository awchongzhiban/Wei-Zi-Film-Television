package com.weizi;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.FileConstants;
import com.weizi.common.domain.dto.UmsAdminParamDto;
import com.weizi.common.domain.entity.UmsAdminEntity;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.IUmsAdminService;
import com.weizi.support.config.uploadimage.ImageUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

/**
 * @date 石添
 * @date 2023/12/24
 */
@RestController
@RequestMapping("admin/admin")
public class AdminController {
    @Value("${file-upload.avatar.max-size}")
    private String maxAvatarSize;

    @Value("${file-upload.avatar.image-types}")
    private List<String> supportedAvatarTypes;

    @Autowired
    private ImageUploader imageUploader;

    private final IUmsAdminService adminService;

    public AdminController(IUmsAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/info")
    public WeiZiResult searchAdminInfo() {
        return WeiZiResult.success(adminService.searchAdminInfo());
    }

    /**
     * 获取菜单列表
     */
    @GetMapping("list")
    public WeiZiResult selectList(UmsAdminParamDto adminParamDto) {
        WeiZiPageResult<UmsAdminEntity> adminList = adminService.selectList(adminParamDto);
        return WeiZiResult.success(adminList);
    }

    /**
     * 获取单个详情
     */
    @GetMapping("getDetail")
    public WeiZiResult searchAdminById(@RequestParam("adminId") Long adminId) {
        if (ObjectUtil.isNull(adminId)) {
            return WeiZiResult.error("adminId不可为空！");
        }
        UmsAdminEntity admin = adminService.searchAdminById(adminId);
        if (ObjectUtil.isNotNull(admin))
            return WeiZiResult.success(admin);
        return WeiZiResult.error("该管理员不存在！");
    }

    /**
     * 保存菜单
     */
    @PostMapping("save")
    public WeiZiResult save(UmsAdminEntity umsAdminEntity) {
        if (ObjectUtil.isNotEmpty(umsAdminEntity))
            return adminService.saveAdmin(umsAdminEntity);
        return WeiZiResult.error("菜单不可为空");
    }

    /**
     * 更新菜单
     */
    @PostMapping("update")
    public WeiZiResult update(UmsAdminEntity umsAdminEntity) {
        if (ObjectUtil.isNotEmpty(umsAdminEntity))
            return adminService.updateAdmin(umsAdminEntity);
        return WeiZiResult.error("菜单不可为空");
    }

    /**
     * 删除菜单（二合一删除单个和多个都可以）
     */
    @GetMapping("delete")
    public WeiZiResult delete(@RequestParam("adminId") Long adminId) {
        if (ObjectUtil.isNotEmpty(adminId)) {
            return adminService.deleteByAdminId(adminId);
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
        if (!supportedAvatarTypes.contains(fileType)) {
            return WeiZiResult.error("文件类型不支持");
        }
        String imageFileName = imageUploader.uploadImage(file, FileConstants.ADMIN_AVATAR);
        System.out.println("imageFileName: "+imageFileName);
        if (ObjectUtil.isNotNull(imageFileName)) {
            if (adminService.updateAdminAvatar(imageFileName, adminId))
                return WeiZiResult.success(file.getBytes());
        }
        // 文件上传逻辑...
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
