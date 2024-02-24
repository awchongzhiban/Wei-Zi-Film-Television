package com.weizi.support.config.uploadimage;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.FileConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class ImageUploader {

    public String uploadImage(MultipartFile file, String imagePath) {
        if (file.isEmpty() || ObjectUtil.isNull(imagePath))
            return null;

        try {
                // 创建文件存放目录（如果不存在）
            File uploadDir = new File(FileConstants.ADMIN_UPLOAD_DIRECTORY + imagePath);
            if (!uploadDir.exists())
                uploadDir.mkdirs(); // 创建目录及其父目录

            // 生成唯一文件名
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            // 创建目标路径
            Path targetPath = Paths.get(FileConstants.ADMIN_UPLOAD_DIRECTORY + imagePath + File.separator + fileName);
            System.out.println("targetPath: "+targetPath);
            // 将文件保存到服务器
            Files.copy(file.getInputStream(), targetPath);

            // 返回成功响应
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
