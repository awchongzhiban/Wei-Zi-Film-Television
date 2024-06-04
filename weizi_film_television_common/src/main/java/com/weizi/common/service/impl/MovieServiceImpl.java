package com.weizi.common.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.domain.dto.MovieDTO;
import com.weizi.common.domain.dto.dataParam.FileInfoParam;
import com.weizi.common.domain.dto.pageParam.MovieParamDTO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.domain.vo.list.MovieVO;
import com.weizi.common.mapper.MovieChunkMapper;
import com.weizi.common.mapper.MovieMapper;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.service.MovieService;
import com.weizi.common.utils.fileOperationUtils.FileOperationUtils;
import com.weizi.common.utils.redis.RedisCacheUtil;
import com.weizi.common.utils.security.WeiZiSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, MoviePO> implements MovieService {

    @Value("${item.poster-path}")
    private String posterPath;

    private final MovieChunkMapper movieChunkMapper;

    private final RedisCacheUtil redisCacheUtil;

    private final FileOperationUtils fileOperationUtils;

    public MovieServiceImpl(MovieChunkMapper movieChunkMapper, RedisCacheUtil redisCacheUtil, RedisCacheUtil redisCacheUtil1, FileOperationUtils fileOperationUtils) {
        this.movieChunkMapper = movieChunkMapper;
        this.redisCacheUtil = redisCacheUtil;
        this.fileOperationUtils = fileOperationUtils;
    }

    @Override
    public WeiZiPageResult<MovieVO> selectList(MovieParamDTO movieParamDto) {
        // 查询总记录数
        int total = baseMapper.countTotal(movieParamDto.getMovieName());
        // 如果总记录数为 0，则直接返回
        if (total == 0) return new WeiZiPageResult<>(null, 0);
        // 计算分页查询的偏移量
        Long pageNum = (movieParamDto.getPageNum() - 1) * movieParamDto.getPageSize();
        // 执行分页查询
        List<MoviePO> records = baseMapper.selectMoviePage(movieParamDto.getMovieName(), pageNum, movieParamDto.getPageSize());
        List<MovieVO> recordsVO = records.stream().map(po -> {
            MovieVO vo = new MovieVO();
            BeanUtils.copyProperties(po, vo);
            // 对头像进行特殊处理
            if (ObjectUtil.isNotNull(po.getPosterUrl())) {
                String imageUrl = posterPath + po.getPosterUrl();
                vo.setPosterUrl(imageUrl);
            }
            return vo;
        }).toList();
        return new WeiZiPageResult<>(recordsVO, total);
    }

    @Override
    public MovieDTO checkMovieExist(FileInfoParam fileInfoParam) {
        // 先查询是否存在
        MoviePO moviePO = baseMapper.selectByMovieMd5(fileInfoParam.getMovieMd5());

        if (ObjectUtil.isNull(moviePO))
            return null;

        // 存在并且没合并和缓存不存在的时候就执行缓存
        if (!moviePO.getIsMerge() && ObjectUtil.isNull(redisCacheUtil.getCacheMapValue("movieNoMerge", moviePO.getMovieId().toString())))
            redisCacheUtil.setCacheMapValue("movieNoMerge", moviePO.getMovieId().toString(), JSONObject.toJSONString(moviePO));

        // PO转DTO
        MovieDTO movieDTO = new MovieDTO();
        BeanUtils.copyProperties(moviePO, movieDTO);
        return movieDTO;
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
            // 如果刚好是播放状态并且已经合并，则不执行删除
            if (!moviePO.getIsPlayer() && moviePO.getIsMerge()) return false;
            // 根据id执行删除
            baseMapper.deleteById(movieId);
            movieChunkMapper.deleteByMovieId(movieId);
            redisCacheUtil.delCacheMapValue("movieNoMerge", movieId.toString());
            // 删除所有的临时分片文件
            fileOperationUtils.removeSliceFiles(movieMd5, totalIndex);
            // 删除单个文件
            fileOperationUtils.removeFile(movieMd5 + "." + moviePO.getFileType());
            return true;
        } catch (Exception e) {
            log.error("Error during deleting movie: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public MovieDTO selectByMovieMd5(String movieMd5) {
        MoviePO moviePO = baseMapper.selectByMovieMd5(movieMd5);
        // PO转DTO
        MovieDTO movieDTO = new MovieDTO();
        BeanUtils.copyProperties(moviePO, movieDTO);
        return movieDTO;
    }

    @Override
    public void updateIsPlayerByMovieMd5(String movieMd5) {
        baseMapper.updateIsPlayerByMovieMd5(movieMd5);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteMergeMovie(String movieMd5) {
        MoviePO moviePO = baseMapper.selectByMovieMd5(movieMd5);
        if (moviePO != null) {
            // 暂时先只能删除可以播放的文件
            if (!moviePO.getIsPlayer()) return false;
            Long movieId = moviePO.getMovieId();
            // 根据id执行删除
            baseMapper.deleteById(movieId);
            // 删除单个文件
            fileOperationUtils.removeFile(movieMd5 + "." + moviePO.getFileType());
            // 删除文件夹和文件
            fileOperationUtils.deleteMovieSlicesFromMinio(movieMd5);
        }
        return true;
    }

    /**
     * 获取播放的MD5
     * @param movieMd5
     * @return
     */
    @Override
    public String getMovieUrl(String movieMd5) {
        MovieDTO movieDTO = selectByMovieMd5(movieMd5);
        /*如果影片数据不为空且为可播放那就直接输出一个新的md5保存到redis中并且最多保存十个小时，
        超过就重新请求然后重新申请*/
        if (movieDTO != null && movieDTO.getIsPlayer()) {
            String movieKey = "movie:" + WeiZiSecurityUtil.getAuthentication().getPrincipal() + UUID.randomUUID();
            // 将key和MD5存入Redis存十个小时
            redisCacheUtil.setCacheObject(movieKey, movieDTO.getMovieMd5(), 10, TimeUnit.HOURS);
            return movieKey;
        }
        return null;
    }
}
