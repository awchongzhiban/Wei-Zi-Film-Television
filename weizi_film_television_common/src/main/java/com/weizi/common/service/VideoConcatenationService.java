package com.weizi.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VideoConcatenationService {

    @Value("${video.movie-folder}")
    private String movieFolder;
    @Value("${ffmpeg.ffprobe-exe}")
    private String ffprobeExePath;
    private static final int TARGET_DURATION_SECONDS = 15;

    public void concatenateChunksToM3U8(List<String> chunkFilePaths, Long movieId, String fileName) throws Exception {
        Path outputFolderPath = Paths.get(movieFolder, movieId + "_" + fileName);
        Files.createDirectories(outputFolderPath);

        List<String> currentSegmentFiles = new ArrayList<>();
        double currentSegmentDuration = 0;

        for (String filePath : chunkFilePaths) {
            double chunkDuration = getVideoDuration(filePath);
            
            // 如果加上当前切片时长超出15秒，则处理当前片段并重置
            if (currentSegmentDuration + chunkDuration > TARGET_DURATION_SECONDS) {
                generateM3U8File(currentSegmentFiles, outputFolderPath, currentSegmentDuration);
                currentSegmentFiles.clear();
                currentSegmentDuration = 0;
            }

            // 添加当前切片到当前片段列表
            currentSegmentFiles.add(filePath);
            currentSegmentDuration += chunkDuration;
        }

        // 处理最后一个未满15秒的片段
        if (!currentSegmentFiles.isEmpty()) {
            generateM3U8File(currentSegmentFiles, outputFolderPath, currentSegmentDuration);
        }
    }

    private void generateM3U8File(List<String> segmentFiles, Path outputFolderPath, double segmentDuration) throws IOException {
        // 这里简化处理，实际上需要使用ffmpeg生成.ts文件和.m3u8清单
        String playlistName = String.format("segment_%02d.m3u8", segmentFiles.size());
        Path playlistPath = outputFolderPath.resolve(playlistName);
        
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(playlistPath))) {
            writer.println("#EXTM3U");
            writer.println("#EXT-X-VERSION:3");
            for (String filePath : segmentFiles) {
                // 假设每个切片直接转换为.ts文件名，实际需要ffmpeg转换
                String tsFileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf('.')).concat(".ts");
                writer.println("#EXTINF:" + String.format("%.3f", segmentDuration / segmentFiles.size()) + ",");
                writer.println(tsFileName);
            }
            writer.println("#EXT-X-ENDLIST");
        }
    }

    /**
     * 使用ffprobe获取视频文件的时长（秒）。
     *
     * @param filePath 视频文件路径
     * @return 视频时长（秒）
     * @throws Exception 如果执行ffprobe命令失败或解析输出时出错
     */
    public double getVideoDuration(String filePath) throws Exception {
    log.info("Attempting to get duration for video at path: {}", filePath);

    ProcessBuilder pb = new ProcessBuilder(
            ffprobeExePath,
            "-v", "error",
            "-select_streams", "v:0",
            "-show_entries", "stream=duration",
            "-of", "default=noprint_wrappers=1:nokey=1",
            filePath
    );

    log.debug("Executing ffprobe command: {}", String.join(" ", pb.command()));

    Process process = pb.start();

    try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
        StringBuilder errorSB = new StringBuilder();
        String errorLine;
        while ((errorLine = errorReader.readLine()) != null) {
            errorSB.append(errorLine).append("\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            log.error("ffprobe command failed with exit code: {}. Error details: {}", exitCode, errorSB.toString());
            throw new RuntimeException("Failed to execute ffprobe command", new IOException(errorSB.toString()));
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String durationLine = reader.readLine();

        if (durationLine == null || durationLine.trim().isEmpty()) {
            throw new IOException("No duration information found in ffprobe output.");
        }

        double duration = Double.parseDouble(durationLine.trim());
        log.info("Video duration: {} seconds", duration);
        return duration;
    } catch (IOException | InterruptedException e) {
        log.error("Error getting video duration", e);
        throw new Exception("Error during video processing", e);
    }
}

}
