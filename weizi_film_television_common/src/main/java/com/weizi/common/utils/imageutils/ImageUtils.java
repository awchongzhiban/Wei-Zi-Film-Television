package com.weizi.common.utils.imageutils;

import cn.hutool.core.util.ObjectUtil;
import com.weizi.common.constants.FileConstants;
import com.weizi.common.utils.MinioUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Component
public class ImageUtils {

    private final MinioUtils minioUtils;

    public ImageUtils(MinioUtils minioUtils) {
        this.minioUtils = minioUtils;
    }

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
    public void deleteImage(String imageName, String imagePath) {
        try {
            // 构建要删除的图片文件路径
            String imagePathToDelete = FileConstants.ADMIN_UPLOAD_DIRECTORY + imagePath + File.separator + imageName;
            // 创建文件对象
            File imageFileToDelete = new File(imagePathToDelete);

            // 检查文件是否存在并删除
            if (imageFileToDelete.exists() && imageFileToDelete.delete()) {
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public long parseSize(String sizeStr) {
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

    // 删除图片并保存新图片
    public boolean deleteAndSaveImage(String objectName, String deleteObjectName, MultipartFile file, String bucketName) throws IOException {
        // 将MultipartFile转换为临时File对象
        File tempFile = convertMultipartFileToFile(file);
        try {
            if (deleteObjectName != null) minioUtils.removeFile(bucketName, deleteObjectName);
            // 使用临时文件进行上传
            boolean uploadResult = minioUtils.uploadFileDir(bucketName, objectName, tempFile);
            // 根据上传结果决定是否删除临时文件
            if (uploadResult) {
                // 删除临时文件（可选：确保上传成功后再删除）
                Files.delete(tempFile.toPath());
            }
            return uploadResult;
        } catch (Exception e) {
            // 处理异常，可能需要清理临时文件
            Files.deleteIfExists(tempFile.toPath());
            throw e; // 再次抛出异常，以便上层调用者能感知到错误
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("temp-", ".upload");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }


    public String generatePresignedGetObjectUrl(String bucketNameMoviePoster, String objectName) {
        return minioUtils.generatePresignedGetObjectUrl(bucketNameMoviePoster, objectName);
    }
}
