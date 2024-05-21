package com.weizi.common.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.domain.dto.dataParam.FileInfoParam;
import com.weizi.common.domain.po.MovieChunkPO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.mapper.MovieChunkMapper;
import com.weizi.common.mapper.MovieMapper;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.MovieService;
import com.weizi.common.utils.fileUtils.FileUtils;
import com.weizi.common.utils.redis.RedisCacheUtil;
import com.weizi.common.utils.security.WeiZiSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, MoviePO> implements MovieService {

    private final MovieChunkMapper movieChunkMapper;
    private final RedisCacheUtil redisCacheUtil;
    private final FileUtils fileUtils;

    public MovieServiceImpl(MovieChunkMapper movieChunkMapper, RedisCacheUtil redisCacheUtil, FileUtils fileUtils) {
        this.movieChunkMapper = movieChunkMapper;
        this.redisCacheUtil = redisCacheUtil;
        this.fileUtils = fileUtils;
    }

    @Override
    public MoviePO checkMovieExist(FileInfoParam fileInfoParam) {
        // 先查询是否存在
        MoviePO moviePO = baseMapper.selectByMovieMd5(fileInfoParam.getMovieMd5());
        // 存在并且缓存不存在的时候就执行缓存
        if (ObjectUtil.isNotNull(moviePO) && ObjectUtil.isNull(redisCacheUtil.getCacheMapValue("movieNoMerge", moviePO.getMovieId().toString())))
            redisCacheUtil.setCacheMapValue("movieNoMerge", moviePO.getMovieId().toString(), JSONObject.toJSONString(moviePO));
        return moviePO;
    }

    @Override
    public Long saveMovie(FileInfoParam fileInfoParam) {
        MoviePO moviePO = new MoviePO();
        moviePO.setMovieName(fileInfoParam.getFileName());
        moviePO.setFileType(fileInfoParam.getFileType());
        moviePO.setFileSize(fileInfoParam.getFileSize());
        moviePO.setMovieMd5(fileInfoParam.getMovieMd5());
        moviePO.setMovieShardTotal(fileInfoParam.getMovieShardTotal());
        moviePO.setIsMerge(false);
        moviePO.setAdminId(WeiZiSecurityUtil.getAdminId());
        try {
            if (baseMapper.insert(moviePO) > 0) {
                redisCacheUtil.setCacheMapValue("movieNoMerge", moviePO.getMovieId().toString(), JSONObject.toJSONString(moviePO));
                return moviePO.getMovieId();
            }
        } catch (Exception e) {
            log.info("saveMovie ServiceImpl 插入失败！原因: {}", e.getMessage());
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeMovieCache(Long movieId) {
        if (baseMapper.updateMergeChunksByMovieId(movieId, WeiZiSecurityUtil.getAdminId()) > 0) {
            try {
                movieChunkMapper.deleteByMovieId(movieId);
                redisCacheUtil.delCacheMapValue("movieNoMerge", movieId.toString());
                return true;
            } catch (Exception e) {
                log.error("Error during merging chunks: {}", e.getMessage());
            }
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteMovie(Long movieId, String movieMd5, int totalIndex) {
        try {
            MoviePO moviePO = baseMapper.selectByMovieMd5(movieMd5);
            baseMapper.deleteById(movieId);
            movieChunkMapper.deleteByMovieId(movieId);
            redisCacheUtil.delCacheMapValue("movieNoMerge", movieId.toString());
            // 删除所有的临时分片文件
            fileUtils.removeSliceFiles(movieMd5, totalIndex);
            // 删除单个文件
            fileUtils.removeFile(movieMd5 + "." + moviePO.getFileType());
            return true;
        } catch (Exception e) {
            log.error("Error during deleting movie: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public MoviePO selectByMovieMd5(String movieMd5) {
        return baseMapper.selectByMovieMd5(movieMd5);
    }
}
