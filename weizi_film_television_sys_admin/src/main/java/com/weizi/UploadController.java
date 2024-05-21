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
import com.weizi.common.domain.dto.dataParam.FileInfoParam;
import com.weizi.common.domain.po.MovieChunkPO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.MovieChunkService;
import com.weizi.common.service.MovieService;
import com.weizi.common.utils.fileUtils.FileUtils;
import com.weizi.common.utils.redis.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Slf4j
@RestController
@RequestMapping("/admin/upload")
public class UploadController {

    private final RedisCacheUtil redisCacheUtil;
    private final MovieService movieService;
    private final MovieChunkService movieChunkService;
    private final FileUtils fileUtils;

    @Value("${app.allowed-file-types}")
    private List<String> allowedFileTypes;

    public UploadController(RedisCacheUtil redisCacheUtil, MovieService movieService, MovieChunkService movieChunkService, FileUtils fileUtils) {
        this.redisCacheUtil = redisCacheUtil;
        this.movieService = movieService;
        this.movieChunkService = movieChunkService;
        this.fileUtils = fileUtils;
    }

    /**
     * 验证文件名和类型，获取文件ID
     *
     * @param fileInfoParam 文件信息参数
     * @return 验证结果
     */
    @PostMapping(value = "/verifyFileNameAndTypeAndGetFileId")
    public WeiZiResult verifyFileNameAndTypeAndGetFileId(@Valid @RequestBody FileInfoParam fileInfoParam) {
        if (!allowedFileTypes.contains(fileInfoParam.getFileType()))
            return WeiZiResult.error("文件类型错误，请重新上传！");
        // 初始化变量
        boolean isExist = false;
        boolean isUpload = true;
        Long movieId;
        int movieIndex = 0;

        // 检查电影是否已存在
        MoviePO moviePO = movieService.checkMovieExist(fileInfoParam);

        // 如果存在，处理上传逻辑
        if (moviePO != null) {
            isExist = true;
            isUpload = !moviePO.getIsMerge();
            movieId = moviePO.getMovieId();
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
    @PostMapping("/breakpointUpload")
    public WeiZiResult breakpointUpload(
            @NotNull(message = "movieFile不能为空") @RequestParam("movieFile") MultipartFile movieFile,
            @NotNull(message = "movieId不能为空") @RequestParam("movieId") Long movieId,
            @NotNull(message = "shardIndex不能为空") @RequestParam("shardIndex") int shardIndex,
            @NotNull(message = "movieMd5不能为空") @RequestParam("movieMd5") String movieMd5) {
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
        if (ObjectUtil.isNull(chunkPO))
            return WeiZiResult.error("请删除后重新上传");
        try {
            MoviePO moviePO = JSONObject.parseObject(moviePOString, MoviePO.class);
            // 检查切片索引是否一致
            if (chunkPO.getMovieShardIndex() != (shardIndex - 1) && moviePO.getMovieShardTotal() != (shardIndex + 1)) {
                return WeiZiResult.error("切片索引异常，请刷新后重新上传");
            }
            // 保存或更新切片到磁盘和数据库
            int movieShardTotal = moviePO.getMovieShardTotal();
            if (fileUtils.saveChunkToTempDir(movieMd5, shardIndex, movieFile)) {
                MovieChunkPO movieChunk = new MovieChunkPO();
                movieChunk.setMovieId(movieId);
                movieChunk.setMovieShardIndex(shardIndex);
                log.info("breakpointUpload shardIndex: {}|{}",shardIndex, moviePO);
                // 先尝试更新，如果没找到记录，则插入
                if (!movieChunkService.updateByMovieId(movieChunk)) {
                    // 如果更新失败，则插入，并且必须为第一条数据才能插入否则返回错误信息并且删除所有切片文件
                    if (shardIndex != 0) {
                        // 删除分片文件
                        fileUtils.removeSliceFiles(movieMd5, movieShardTotal);
                        return WeiZiResult.error("请删除后重新上传");
                    }
                    movieChunkService.save(movieChunk);
                }
            }
            if (shardIndex < movieShardTotal - 1) {
                return WeiZiResult.success(shardIndex);
            }
            // 合并文件
            if (fileUtils.composeFile(movieMd5, movieMd5 + "." + moviePO.getFileType(), movieShardTotal)) {
                if (movieService.removeMovieCache(movieId)) {
                    // 删除分片文件
                    fileUtils.removeSliceFiles(movieMd5, movieShardTotal);
                    redisCacheUtil.delCacheMapValue("movieNoMerge", movieId.toString());
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

    @PostMapping("/removeFile")
    public WeiZiResult removeFile(@NotNull(message = "movieMd5不能为空") @RequestParam(name = "movieMd5") String movieMd5) {
        try {
            MoviePO moviePO = movieService.selectByMovieMd5(movieMd5);
            if (ObjectUtil.isNull(moviePO))
                return WeiZiResult.error("无法找到电影信息");
            if (movieService.deleteMovie(moviePO.getMovieId(), moviePO.getMovieMd5(), moviePO.getMovieShardTotal()))
                return WeiZiResult.success();
            log.error("Failed to delete movie: {}", movieMd5);
        } catch (Exception e) {
            log.error("Failed to delete movie: {}", e.getMessage());
        }
        return WeiZiResult.error("删除失败");
    }

    /*@PostMapping("/createHLSPlayList")
    public WeiZiResult createHLSPlayList(@NotNull(message = "movieId不能为空") @RequestParam(name = "movieId") Long movieId) {
        try {
            // 检查Redis中是否存在电影对象
            String moviePOString = redisCacheUtil.getCacheMapValue("movieNoMerge", movieId.toString());
            if (moviePOString == null) {
                return WeiZiResult.error("无法找到电影信息，请确保电影已上传切片");
            }

            // 将JSON字符串转换为MoviePO对象
            MoviePO moviePO = JSONObject.parseObject(moviePOString, MoviePO.class);
            if (moviePO == null) {
                return WeiZiResult.error("无法解析电影信息，请确保数据格式正确");
            }

            // 创建HLS播放列表
            fileUtils.createHLSPlayList(movieId, moviePO.getMovieName());
            if (movieService.composeFile(movieId))
                return WeiZiResult.success();
            return WeiZiResult.error("上传异常，请重试");
        } catch (JSONException e) {
            log.error("解析JSON对象时出错: ", e);
            return WeiZiResult.error("解析JSON错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("未知错误: ", e);
            return WeiZiResult.error("发生错误: " + e.getMessage());
        }
    }*/
}

