package com.weizi.common.utils.moviePosterUtils;

import com.weizi.common.constants.FileConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class MoviePosterUtils {
    @Value("${item.static-path}")
    private String staticPath;

    @Value("${item.poster-path}")
    private String posterPath;

    // 上传海报方法
    public String uploadPoster(MultipartFile file) {
        if (file.isEmpty())
            return null;

        try {
            // 生成唯一文件名
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            // 创建目标路径
            Path targetPath = Paths.get(staticPath + posterPath + fileName);
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

    // 删除海报方法
    public boolean deletePoster(String imageName, String imagePath) {
        try {
            // 构建要删除的海报文件路径
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
}
