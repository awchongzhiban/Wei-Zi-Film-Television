package com.weizi.common.taskProcessors;

import com.weizi.common.service.MovieService;
import com.weizi.common.utils.fileOperationUtils.FileOperationUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.*;


/**
 * 用于给上传成功的文件用ffmpeg进行切片
 */
@Slf4j
@Component
public class SequentialFileProcessor {

    @Value("${sequential-file-processor.max-threads}")
    private int maxThreads;
    private final MovieService movieService;
    private ExecutorService executorService;
    private final FileOperationUtils fileOperationUtils;
    private static final int MAX_RETRY_TIMES = 4; // 最大重试次数

    public SequentialFileProcessor(MovieService movieService, FileOperationUtils fileOperationUtils) {
        this.movieService = movieService;
        this.fileOperationUtils = fileOperationUtils;
    }

    /**
     * 初始化线程池，放在@PostConstruct注解的方法中，确保在所有依赖注入完成后执行yml里的配置。
     */
    @PostConstruct
    public void initExecutorService() {
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public void processFilesSequentially(String movieMd5, String objectName, int retryCount) {
        if (retryCount > MAX_RETRY_TIMES) {
            log.error("Exceeded max retry times for processing file: movieMd5={}, objectName={}", movieMd5, objectName);
            return;
        }

        try {
            Future<?> future = executorService.submit(() -> {
                try (var ignored = new Closeable() {
                    @Override
                    public void close() {
                        Thread.currentThread().interrupt(); // 如果需要中断，恢复中断状态
                    }
                }) {
                    log.info("开始下载和切片文件: movieMd5={}, objectName={}", movieMd5, objectName);
                    if (fileOperationUtils.downloadAndSliceFile(movieMd5, objectName)) {
                        log.info("文件切片成功: movieMd5={}, objectName={}", movieMd5, objectName);
                        movieService.updateIsPlayerByMovieMd5(movieMd5);
                    } else {
                        fileOperationUtils.cleanupSlice(movieMd5);
                        processFilesSequentially(movieMd5, objectName, retryCount + 1);
                    }
                } catch (IOException | InterruptedException e) {
                    if (Thread.currentThread().isInterrupted()) {
                        log.error("File processing interrupted: {}", e.getMessage());
                        return; // 中断请求，停止处理
                    } else {
                        log.error("Error downloading and slicing file: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // 阻塞，直到任务完成或被中断
            future.get();
        } catch (ExecutionException | InterruptedException e) {
            Throwable cause = e.getCause();
            if (cause instanceof InterruptedException) {
                log.error("Interrupted while waiting for file processing: {}", cause.getMessage());
            } else {
                log.error("Error during file processing: {}", cause.getMessage());
            }
        }
    }


    public void shutdown() {
        executorService.shutdown();
    }
}
