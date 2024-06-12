package com.weizi.common.utils.fileOperationUtils;

import com.weizi.common.config.MinioConfig;
import com.weizi.common.service.VideoConcatenationService;
import com.weizi.common.utils.MinioUtils;
import io.minio.ComposeSource;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Component
public class FileOperationUtils {
    // 视频切片文件路径
    @Value("${video.slice-movie-folder}")
    private String videoSliceMovieFolder;
    // 存储转码视频的文件夹
    @Value("${video.movie-folder}")
    private String videoMovieFolder;
    // 存储转码视频的文件夹
    @Value("${ffmpeg.ffmpeg-exe}")
    private String ffmpegExePath;
    // 存储转码视频的文件夹
    @Value("${ffmpeg.ffprobe-exe}")
    private String ffprobeExePath;
    // 切片时长
    @Value("${ffmpeg.movie-slice-duration}")
    private int sliceDuration;


    private final VideoConcatenationService videoConcatenationService;
    private final MinioUtils minioUtils;
    private final MinioConfig minioConfig;

    public FileOperationUtils(VideoConcatenationService videoConcatenationService, MinioUtils minioUtils, MinioConfig minioConfig) {
        this.videoConcatenationService = videoConcatenationService;
        this.minioUtils = minioUtils;
        this.minioConfig = minioConfig;
    }

    // 保存切片到临时文件夹
    public boolean saveChunkToTempDir(String movieMd5, int shardIndex, MultipartFile file) {
        return minioUtils.uploadFile(minioConfig.getBucketNameSlice(), movieMd5, shardIndex, file);
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

    // 保存转换后的视频切片到文件夹
    public boolean saveVideoShardToFileSystem(String movieMd5, MultipartFile file) {
        return minioUtils.uploadFile(minioConfig.getBucketNameSliceMovie(), movieMd5, null, file);
    }

    /**
     * 下载并切片文件
     *
     * @param objectName 对象名称
     */
    public boolean downloadAndSliceFile(String movieMd5, String objectName) throws Exception {
        File downloadedFile = new File(videoMovieFolder, objectName);
        try (InputStream objectStream = minioUtils.getFileStream(minioConfig.getBucketName(), objectName)) {
            FileUtils.copyInputStreamToFile(objectStream, downloadedFile);
        } catch (Exception e) {
            log.error("文件下载失败|downloadAndSliceFile|参数bucketName:{},objectName:{}", minioConfig.getBucketName(), objectName, e);
        }
        // 使用ffmpeg进行切片
        boolean slicingSuccessful = sliceFileWithFFmpeg(movieMd5, downloadedFile);
        if (slicingSuccessful) {
            // 删除原始文件
            if (!downloadedFile.delete())
                log.error("文件删除失败，可能是因为文件不存在或没有足够的权限：{}", objectName);
            // 执行分片转码完成后的上传文件到Minio
            if (!uploadSlicesToMinio(movieMd5))
                log.error("切片文件上传至Minio失败: {}", movieMd5);
            // 最后删除切片文件内的数据
            cleanupSlice(movieMd5);
        } else {
            log.error("文件切片失败，原始文件未被删除: {}", objectName);
        }
        return slicingSuccessful;
    }

    /**
     * 使用ffmpeg切片文件
     *
     * @param inputFile    输入文件
     * @return 切片成功返回true，失败返回false
     */
    private boolean sliceFileWithFFmpeg(String movieMd5, File inputFile) throws Exception {
        // 创建movieMd5子目录，如果不存在
        File outputSubDirectory = new File(videoSliceMovieFolder, movieMd5);
        outputSubDirectory.mkdirs();

        // 修改输出目录为movieMd5子目录
        Path outputDirectory = outputSubDirectory.toPath();

        // 准备用于HLS切片的ProcessBuilder
        ProcessBuilder pb = createHlsSegmentationProcessBuilder(inputFile, outputDirectory, sliceDuration);
        pb.redirectErrorStream(true); // 合并错误流和标准输出流
        Process process = pb.start();

        // 读取合并后的输出流
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("FFmpeg output: {}", line);
            }
        } catch (IOException e) {
            log.error("Error reading FFmpeg output: {}", e.getMessage());
        }

        // 等待ffmpeg进程完成
        process.waitFor();
        log.debug("FFmpeg进程等待完成");

        // 检查ffmpeg的退出状态
        int exitValue = process.exitValue();
        if (exitValue != 0) {
            log.error("FFmpeg执行失败，退出码: {}", exitValue);
        }
        return exitValue == 0; // 返回切片是否成功
    }

    /**
     * 创建一个ProcessBuilder用于HLS（HTTP Live Streaming）视频分段处理。
     * <p>
     * 该方法配置了一个ProcessBuilder，用于使用FFmpeg将输入视频文件切割成较小的片段（分片），并生成一个m3u8索引文件，
     * 以便于在Web上进行流式传输。
     * </p>
     *
     * @param inputFile 输入视频文件，FFmpeg将对此文件进行分段处理。
     * @param outputDirectory 分段视频和m3u8索引文件的输出目录。
     * @param sliceDuration 每个视频分片的持续时间（单位：秒）。
     * @return 配置好的ProcessBuilder实例，可用于执行HLS分段任务。
     */
    private ProcessBuilder createHlsSegmentationProcessBuilder(File inputFile, Path outputDirectory, long sliceDuration) throws Exception {
        // 使用ffprobe获取音频轨道信息
        String[] audioTracks = getAudioTracks(inputFile);
        // 假设我们优先选择AAC编码的音轨，如果没有AAC，则选择第一个音轨
        String audioTrackOption = determineAudioTrackOption(audioTracks);
        // 使用FFmpeg进行HLS分段的命令行参数配置
        ProcessBuilder pb = new ProcessBuilder(
                ffmpegExePath,
                "-i", inputFile.getAbsolutePath(),
                "-map", "0:v:0",
                "-map", audioTrackOption,
                "-c:v", "libx264", // 需要重新编码以应用force_key_frames，因为'copy'不会改变视频流
                "-c:a", "aac", // 如果原音频编码不支持直接复制，这里指定重新编码为AAC
                "-force_key_frames", "expr:gte(t,n_forced*" + sliceDuration + ")", // 强制每秒开始的关键帧
                "-hls_time", String.valueOf(sliceDuration),
                "-hls_list_size", "0",
                "-hls_segment_filename", outputDirectory.resolve("%d.ts").toString(),
                outputDirectory.resolve("output.m3u8").toString()
        );
        log.trace("构造FFmpeg命令详情: {}", pb.command()); // 这里记录详细的命令构建过程
        return pb;
    }

    /**
     * 清理切片(成功和失败都要清理)
     *
     */
    public void cleanupSlice(String movieMd5) {
        File sliceOutputDirectory = new File(videoSliceMovieFolder, movieMd5);
        if (sliceOutputDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(sliceOutputDirectory);
                log.info("已删除切片的输出目录：{}", sliceOutputDirectory.getAbsolutePath());
            } catch (IOException e) {
                log.error("删除的切片输出目录时出错：{}", sliceOutputDirectory.getAbsolutePath(), e);
            }
        }
    }

    /**
     * 清理下载文件和切片 TODO 暂时先留着后续加入合并或者切片的时候也可以删除的功能时可用
     *
     */
    public void cleanupDownloadAndSlice(String movieMd5, String objectName) {
        File downloadedFile = new File(videoMovieFolder, objectName);
        if (downloadedFile.exists()) {
            try {
                FileUtils.deleteDirectory(downloadedFile);
                log.info("downloadedFile 已删除切片的输出目录：{}", downloadedFile.getAbsolutePath());
            } catch (IOException e) {
                log.error("downloadedFile 删除的切片输出目录时出错：{}", downloadedFile.getAbsolutePath(), e);
            }
        }
        cleanupSlice(movieMd5);
    }

    /**
     * 上传切片到Minio
     */
    private boolean uploadSlicesToMinio(String movieMd5) {
        File sliceDirectory = new File(videoSliceMovieFolder, movieMd5);
        if (!sliceDirectory.exists() || !sliceDirectory.isDirectory()) {
            log.error("切片目录不存在或不是一个目录: {}", sliceDirectory.getAbsolutePath());
            return false;
        }
        try {
            // 遍历切片目录下的所有文件
            File[] slices = sliceDirectory.listFiles();
            if (slices != null) {
                for (File slice : slices) {
                    // 假设您的uploadFileToMinio方法接受文件名作为objectName的一部分
                    String objectNameInMinio = movieMd5 + "/" + slice.getName();
                    boolean uploadSuccess = minioUtils.uploadFileDir(minioConfig.getBucketNameSliceMovie(), objectNameInMinio, slice);
                    if (!uploadSuccess) {
                        log.error("上传文件至Minio失败: {}", slice.getName());

                        deleteSlicesFromMinio(movieMd5, slices);
                        return false; // 如果任何一个文件上传失败，提前结束并返回false
                    }
                }
            }
        } catch (Exception e) {
            log.error("上传切片至Minio时发生异常: ", e);
            return false;
        }
        return true;
    }

    private String[] getAudioTracks(File inputFile) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                ffprobeExePath,
                "-i", inputFile.getAbsolutePath(),
                "-show_entries", "stream=codec_name,codec_long_name",
                "-select_streams", "a",
                "-of", "csv=p=0"
        );

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder tracksInfo = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            tracksInfo.append(line).append("\n");
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("ffprobe exited with error code " + exitCode);
        }

        // 解析音频轨道信息
        return tracksInfo.toString().split("\n");
    }

    private String determineAudioTrackOption(String[] audioTracks) {
        // 示例逻辑：优先选择AAC编码的音轨，如果没有找到，则选择第一个音轨
        OptionalInt aacTrackIndex = IntStream.range(0, audioTracks.length)
                .filter(i -> audioTracks[i].contains("aac"))
                .findFirst();

        return aacTrackIndex.isPresent()
                ? "0:a:" + aacTrackIndex.getAsInt()
                : "0:a:0";
    }

    private void deleteSlicesFromMinio(String movieMd5, File[] slices) {
        // 删除所有的临时分片文件
        List<DeleteObject> delObjects = Stream.of(slices)
                .map(file -> new DeleteObject(movieMd5.concat("/").concat(file.getName())))
                .toList();
        minioUtils.removeSliceFiles(minioConfig.getBucketNameSliceMovie(), delObjects);
    }

    public void deleteMovieSlicesFromMinio(String movieMd5) {
        Set<String> fileObjects = minioUtils.getFileObjects(minioConfig.getBucketNameSliceMovie(), movieMd5);
        List<DeleteObject> delObjects = fileObjects.stream()
                .map(DeleteObject::new)
                .toList();
        minioUtils.removeSliceFiles(minioConfig.getBucketNameSliceMovie(), delObjects);
    }

    public InputStream getFileStreamByMovieMd5(String movieMd5) {
        return minioUtils.getFileStream(minioConfig.getBucketNameSliceMovie(), movieMd5.concat("/output.m3u8"));
    }

    public InputStream getFileStreamTsByMovieMd5(String movieMd5, String chunk) {
        return minioUtils.getFileStream(minioConfig.getBucketNameSliceMovie(), movieMd5.concat("/").concat(chunk));
    }
}
