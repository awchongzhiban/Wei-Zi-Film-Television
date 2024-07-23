package com.weizi;

/**
 * @author AWei
 * @create 2023-05-09 10:33
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.weizi.common.domain.dto.MovieDTO;
import com.weizi.common.domain.dto.dataParam.FileInfoParam;
import com.weizi.common.domain.po.MovieChunkPO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.MovieChunkService;
import com.weizi.common.service.MovieService;
import com.weizi.common.taskProcessors.SequentialFileProcessor;
import com.weizi.common.utils.fileOperationUtils.FileOperationUtils;
import com.weizi.common.utils.redis.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/admin/upload")
public class UploadController {

    private final RedisCacheUtil redisCacheUtil;
    private final MovieService movieService;
    private final MovieChunkService movieChunkService;
    private final FileOperationUtils fileOperationUtils;
    private final SequentialFileProcessor sequentialFileProcessor;

    @Value("${app.allowed-file-types}")
    private List<String> allowedFileTypes;

    public UploadController(RedisCacheUtil redisCacheUtil, MovieService movieService, MovieChunkService movieChunkService, FileOperationUtils fileOperationUtils, SequentialFileProcessor sequentialFileProcessor) {
        this.redisCacheUtil = redisCacheUtil;
        this.movieService = movieService;
        this.movieChunkService = movieChunkService;
        this.fileOperationUtils = fileOperationUtils;
        this.sequentialFileProcessor = sequentialFileProcessor;
    }

    /**
     * 验证文件名和类型，获取文件ID
     *
     * @param fileInfoParam 文件信息参数
     * @return 验证结果
     */
    @PostMapping(value = "/uploadMovie/verifyFileNameAndTypeAndGetFileId")
    public WeiZiResult verifyFileNameAndTypeAndGetFileId(@Valid @RequestBody FileInfoParam fileInfoParam) {
        if (!allowedFileTypes.contains(fileInfoParam.getFileType()))
            return WeiZiResult.error("文件类型错误，请重新上传！");
        // 初始化变量
        boolean isExist = false;
        boolean isUpload = true;
        Long movieId;
        int movieIndex = 0;

        // 检查电影是否已存在
        MovieDTO movieDTO = movieService.checkMovieExist(fileInfoParam);
        // 如果存在，处理上传逻辑
        if (movieDTO != null) {
            isExist = true;
            isUpload = !movieDTO.getIsMerge();
            movieId = movieDTO.getMovieId();
            MovieChunkPO movieChunkPO = movieChunkService.selectByMovieId(movieId);
            if (movieChunkPO != null) {
                movieIndex = movieChunkPO.getMovieShardIndex() + 1;
            }
        } else {
            // 电影不存在，保存电影信息并获取ID
            movieId = movieService.saveMovie(fileInfoParam);
            if (movieId == null) {
                return WeiZiResult.error("数据异常，请重新上传！");
            }
        }

        // 返回验证结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isExist", isExist);
        resultMap.put("isUpload", isUpload);
        resultMap.put("movieId", movieId);
        resultMap.put("movieIndex", movieIndex);
        return WeiZiResult.success(resultMap);
    }

    /**
     * 断点续传文件
     *
     * @param movieFile     电影文件
     * @param movieId       电影ID
     * @param shardIndex    切片索引
     * @param movieMd5      电影MD5
     * @return 上传结果
     */
    @PostMapping("/uploadMovie/breakpointUpload")
    public WeiZiResult breakpointUpload(
            @Valid @NotNull(message = "movieFile不能为空") @RequestParam("movieFile") MultipartFile movieFile,
            @Valid @NotNull(message = "movieId不能为空") @RequestParam("movieId") Long movieId,
            @Valid @NotNull(message = "shardIndex不能为空") @RequestParam("shardIndex") int shardIndex,
            @Valid @NotNull(message = "movieMd5不能为空") @RequestParam("movieMd5") String movieMd5) {
        // 验证movieId是否有效
        String moviePOString = redisCacheUtil.getCacheMapValue("movieNoMerge", movieId.toString());
        if (moviePOString == null) {
            return WeiZiResult.error("请检查文件是否已存在");
        }

        // 检查切片索引
        if (shardIndex < 0) {
            return WeiZiResult.error("切片索引异常");
        }
        // 检查电影切片表中是否存在该切片
        MovieChunkPO chunkPO = movieChunkService.selectByMovieId(movieId);
        try {
            MoviePO moviePO = JSONObject.parseObject(moviePOString, MoviePO.class);
            // 检查切片索引是否一致 判断条件如果为空且index不为0那就要么数据有问题，要么保存没保存上，另一个判断是判断index必须匹配上一次的值，并且片总数必须匹配
            if ((ObjectUtil.isNull(chunkPO) && shardIndex != 0) || (ObjectUtil.isNotNull(chunkPO) && chunkPO.getMovieShardIndex() != (shardIndex - 1) && moviePO.getMovieShardTotal() != (shardIndex + 1))) {
                return WeiZiResult.error("切片索引异常，请刷新后重新上传");
            }
            // 保存或更新切片到磁盘和数据库
            int movieShardTotal = moviePO.getMovieShardTotal();
            if (fileOperationUtils.saveChunkToTempDir(movieMd5, shardIndex, movieFile)) {
                MovieChunkPO movieChunk = new MovieChunkPO();
                movieChunk.setMovieId(movieId);
                movieChunk.setMovieShardIndex(shardIndex);
                log.info("breakpointUpload shardIndex: {}|{}",shardIndex, moviePO);
                // 先尝试更新，如果没找到记录，则插入
                if (!movieChunkService.updateByMovieId(movieChunk)) {
                    // 如果更新失败，并且必须为第一条数据才能插入否则返回错误信息并且删除所有切片文件
                    if (shardIndex != 0) {
                        // 删除分片文件
                        fileOperationUtils.removeSliceFiles(movieMd5, movieShardTotal);
                        return WeiZiResult.error("请重新上传");
                    }
                    movieChunkService.save(movieChunk);
                }
            }
            if (shardIndex < movieShardTotal - 1) {
                return WeiZiResult.success(shardIndex);
            }
            // 包含拓展名的完整文件名称
            String objectName = movieMd5 + "." + moviePO.getFileType();
            // 合并文件
            if (fileOperationUtils.composeFile(movieMd5, objectName, movieShardTotal)) {
                if (movieService.removeMovieCache(movieId)) {
                    // 删除分片文件
                    fileOperationUtils.removeSliceFiles(movieMd5, movieShardTotal);
                    redisCacheUtil.delCacheMapValue("movieNoMerge", movieId.toString());
                    // 把需要拆分的文件夹放入队列中多线程进行拆分
                    sequentialFileProcessor.processFilesSequentially(movieMd5, objectName, 0);
                    return WeiZiResult.success();
                }
                log.error("Failed update movie {}",moviePO);
            }
            log.error("Failed to compose shards {}",moviePO);
            return WeiZiResult.error("Failed to upload or compose the shards");
        } catch (JSONException e) {
            log.error("Failed to parse JSON string to MoviePO: {}|{}", e.getMessage(), moviePOString);
        }
        return WeiZiResult.error("切片上传失败");
    }

    @PostMapping("/removeMovieFile")
    public WeiZiResult removeMovieFile(@Valid @NotNull(message = "movieMd5不能为空") @RequestParam(name = "movieMd5") String movieMd5) {
        try {
            MovieDTO movieDTO = movieService.selectByMovieMd5(movieMd5);
            if (ObjectUtil.isNull(movieDTO))
                return WeiZiResult.error("无法找到电影信息");
            if (movieService.deleteMovie(movieDTO.getMovieId(), movieDTO.getMovieMd5(), movieDTO.getMovieShardTotal()))
                return WeiZiResult.success();
            log.error("Failed to delete movie: {}", movieMd5);
        } catch (Exception e) {
            log.error("Failed to delete movie: {}", e.getMessage());
        }
        return WeiZiResult.error("删除失败");
    }
}

