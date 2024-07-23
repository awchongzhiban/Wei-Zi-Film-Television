/*
package com.weizi.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

public class FFMpegHelper {
    // 视频切片文件路径
    @Value("${video.slice-movie-folder}")
    private String videoSliceMovieFolder;
    // 存储转码视频的文件夹
    @Value("${video.movie-folder}")
    private String videoMovieFolder;
    // 存储转码视频的文件夹
    @Value("${ffmpeg.ffmpeg-exe}")
    private String ffmpegExePath;
    // 切片时长
    @Value("${ffmpeg.movie-slice-duration}")
    private int sliceDuration;

    public static void moveMoovAtomToFront(String inputFilePath, String tempOutputFilePath) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFilePath,
                "-movflags", "+faststart",
                tempOutputFilePath);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (StringUtils.startsWith(line, "Output #0")) {
                break;
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
        }

        // 替换源文件
        Files.copy(Paths.get(tempOutputFilePath), Paths.get(inputFilePath), StandardCopyOption.REPLACE_EXISTING);
    }
}
*/
