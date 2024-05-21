package com.weizi.common.utils.fileUtils;

import com.weizi.common.config.MinioConfig;
import com.weizi.common.service.VideoConcatenationService;
import com.weizi.common.utils.MinioUtils;
import io.minio.ComposeSource;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class FileUtils {
    // 视频临时文件路径
    @Value("${video.temp-folder}")
    private String videoTempFolder;
    // 存储转码视频的文件夹
    @Value("${video.movie-folder}")
    private String videoMovieFolder;
    // 存储转码视频的文件夹
    @Value("${ffmpeg.ffmpeg-exe}")
    private String ffmpegExePath;


    private final VideoConcatenationService videoConcatenationService;
    private final MinioUtils minioUtils;
    private final MinioConfig minioConfig;

    public FileUtils(VideoConcatenationService videoConcatenationService, MinioUtils minioUtils, MinioConfig minioConfig) {
        this.videoConcatenationService = videoConcatenationService;
        this.minioUtils = minioUtils;
        this.minioConfig = minioConfig;
    }

    // 保存切片到临时文件夹
    public boolean saveChunkToTempDir(String movieMd5, int shardIndex, MultipartFile file) {
        return minioUtils.uploadFile(minioConfig.getBucketNameSlice(), movieMd5, shardIndex, file);
    }

    // TODO 需要加入队列，如果失败就加入到队列中，然后重新创建
    public void createHLSPlayList(Long movieId, String fileName) throws Exception {
        // 1. 列出所有切片文件
        List<String> chunkFilePaths = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(videoTempFolder + movieId + "_" + fileName))) {
            for (Path path : directoryStream) {
                chunkFilePaths.add(path.toString());
            }
        } catch (IOException e) {
            log.error("Failed to list chunk files: " + e.getMessage());
        }

        // 2. 检查是否有切片文件
        if (chunkFilePaths.isEmpty()) {
            log.info("No chunk files found in the folder: " + videoTempFolder);
        }

        // 2. 排序逻辑简化，假设文件名格式固定为 movieId_filename_chunkNumber.mp4
        chunkFilePaths.sort(Comparator.comparingInt(path -> {
            String name = new File(path).getName();
            return Integer.parseInt(name.substring(name.lastIndexOf('_') + 1, name.lastIndexOf('.')));
        }));

        // 合并切片文件
        videoConcatenationService.concatenateChunksToM3U8(chunkFilePaths, movieId, fileName);
    }

    // 合并切片文件
    public boolean composeFile(String movieMd5, String fileName, int totalIndex) {
        // 完成上传从缓存目录合并迁移到正式目录
        List<ComposeSource> sourceObjectList = Stream.iterate(0, i -> ++i)
                .limit(totalIndex)
                .map(i -> ComposeSource.builder()
                        .bucket(minioConfig.getBucketNameSlice())
                        .object(movieMd5.concat("/").concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        log.debug("文件合并|composeFile|参数objectName:{},fullObjectName:{},totalPieces:{}", movieMd5,fileName, totalIndex);
        // 合并操作
        return minioUtils.composeFile(minioConfig.getBucketName(), fileName, sourceObjectList);
    }

    public void removeSliceFiles(String movieMd5, int totalIndex) {
        // 删除所有的临时分片文件
        List<DeleteObject> delObjects = Stream.iterate(0, i -> ++i)
                .limit(totalIndex)
                .map(i -> new DeleteObject(movieMd5.concat("/").concat(Integer.toString(i))))
                .collect(Collectors.toList());
        minioUtils.removeSliceFiles(minioConfig.getBucketNameSlice(), delObjects);
    }

    public void removeFile(String movieFileName) {
        // 删除文件
        minioUtils.removeFile(minioConfig.getBucketName(), movieFileName);
    }
}
