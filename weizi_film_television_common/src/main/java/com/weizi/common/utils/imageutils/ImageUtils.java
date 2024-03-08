package com.weizi.common.utils.imageutils;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.FileConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Component
public class ImageUtils {

    // 上传图片方法
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

    // 删除图片方法
    public boolean deleteImage(String imageName, String imagePath) {
        try {
            // 构建要删除的图片文件路径
            String imagePathToDelete = FileConstants.ADMIN_UPLOAD_DIRECTORY + imagePath + File.separator + imageName;
            // 创建文件对象
            File imageFileToDelete = new File(imagePathToDelete);

            // 检查文件是否存在并删除
            if (imageFileToDelete.exists() && imageFileToDelete.delete()) {
                return true; // 删除成功
            } else {
                return false; // 文件不存在或删除失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 方法用于将图片文件转换为 Base64 编码的字符串
    public String encodeImageToBase64(String imageFilePath) {
        File file = new File(imageFilePath);
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] imageBytes = new byte[(int) file.length()];
            fis.read(imageBytes);
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
