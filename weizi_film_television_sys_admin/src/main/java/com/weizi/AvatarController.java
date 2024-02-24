package com.weizi;

import com.weizi.common.constants.FileConstants;
import com.weizi.common.response.WeiZiResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class AvatarController {
    public WeiZiResult uploadAvatar(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return WeiZiResult.error("Please select a file to upload");
        }

        try {
            // 创建文件存放目录（如果不存在）
            File uploadDir = new File(FileConstants.ADMIN_UPLOAD_DIRECTORY);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // 创建目录及其父目录
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            // 创建目标路径
            Path targetPath = Paths.get(FileConstants.ADMIN_UPLOAD_DIRECTORY + File.separator + fileName);
            // 将文件保存到服务器
            Files.copy(file.getInputStream(), targetPath);

            // 返回成功响应
            return WeiZiResult.success("File uploaded successfully: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return WeiZiResult.error("Failed to upload file");
        }
    }
}
