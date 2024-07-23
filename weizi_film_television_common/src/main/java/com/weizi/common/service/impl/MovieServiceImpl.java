package com.weizi.common.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.config.MinioConfig;
import com.weizi.common.domain.dto.MovieToGenreDTO;
import com.weizi.common.domain.dto.MovieDTO;
import com.weizi.common.domain.dto.dataParam.FileInfoParam;
import com.weizi.common.domain.dto.dataParam.MovieGroupParam;
import com.weizi.common.domain.dto.dataParam.MovieParam;
import com.weizi.common.domain.dto.pageParam.MovieParamDTO;
import com.weizi.common.domain.po.ChannelPO;
import com.weizi.common.domain.po.GenrePO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.domain.vo.list.MovieVO;
import com.weizi.common.mapper.ChannelMapper;
import com.weizi.common.mapper.GenreMapper;
import com.weizi.common.mapper.MovieChunkMapper;
import com.weizi.common.mapper.MovieMapper;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.service.MovieService;
import com.weizi.common.utils.fileOperationUtils.FileOperationUtils;
import com.weizi.common.utils.imageutils.ImageUtils;
import com.weizi.common.utils.redis.RedisCacheUtil;
import com.weizi.common.utils.security.WeiZiSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MovieServiceImpl extends ServiceImpl<MovieMapper, MoviePO> implements MovieService {
    private final MinioConfig minioConfig;
    private final ImageUtils imageUtils;
    private final RedisCacheUtil redisCacheUtil;
    private final FileOperationUtils fileOperationUtils;
    private final MovieChunkMapper movieChunkMapper;
    private final GenreMapper genreMapper;
    private final ChannelMapper channelMapper;

    public MovieServiceImpl(MinioConfig minioConfig, ImageUtils imageUtils, RedisCacheUtil redisCacheUtil, FileOperationUtils fileOperationUtils, MovieChunkMapper movieChunkMapper, GenreMapper genreMapper, ChannelMapper channelMapper) {
        this.minioConfig = minioConfig;
        this.imageUtils = imageUtils;
        this.redisCacheUtil = redisCacheUtil;
        this.fileOperationUtils = fileOperationUtils;
        this.movieChunkMapper = movieChunkMapper;
        this.genreMapper = genreMapper;
        this.channelMapper = channelMapper;
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
        List<MovieDTO> records = baseMapper.selectMoviePage(movieParamDto.getMovieName(), pageNum, movieParamDto.getPageSize());
        List<Long> movieIdList = records.stream().map(MovieDTO::getMovieId).toList();
        List<MovieToGenreDTO> movieToGenreDTOS = genreMapper.selectGenreIdAndNameByMovieIds(movieIdList);
        // 构建一个Map来快速查找每个movieId对应的genreIds
        Map<Long, List<Long>> movieIdToGenreIdsMap = movieToGenreDTOS.stream()
                .collect(Collectors.groupingBy(MovieToGenreDTO::getMovieId,
                        Collectors.mapping(MovieToGenreDTO::getGenreId, Collectors.toList())));
        List<MovieVO> recordsVO = records.stream().map(dto -> {
            MovieVO vo = new MovieVO();
            BeanUtils.copyProperties(dto, vo);
            if (dto.getMainPoster() != null) {
                vo.setMainPoster(imageUtils.generatePresignedGetObjectUrl(minioConfig.getBucketNameMovieMainPoster(), dto.getMovieMd5().concat("/" + dto.getMainPoster())));
            }
            if (dto.getPoster() != null) {
               vo.setPoster(imageUtils.generatePresignedGetObjectUrl(minioConfig.getBucketNameMoviePoster(), dto.getMovieMd5().concat("/" + dto.getPoster())));
            }
            // 根据movieId从map中获取对应的genreIds并设置到vo中
            vo.setGenreIdList(movieIdToGenreIdsMap.getOrDefault(dto.getMovieId(), Collections.emptyList()));
            return vo;
        }).toList();
        return new WeiZiPageResult<>(recordsVO, total);
    }

    @Override
    public Map<Long, String> selectAllChannels() {
        List<ChannelPO> channels = channelMapper.selectAllChannels(); // 假设selectAllChannels返回所有Channel的列表
        return channels.stream()
                .collect(Collectors.toMap(ChannelPO::getChannelId, ChannelPO::getChannelName));
    }

    @Override
    public Map<Long, String> selectAllGenres() {
        List<GenrePO> genres = genreMapper.selectAllGenres(); // 假设selectAllGenres返回所有Genre的列表
        return genres.stream()
                .collect(Collectors.toMap(GenrePO::getGenreId, GenrePO::getGenreName));
    }

    @Override
    public MovieDTO checkMovieExist(FileInfoParam fileInfoParam) {
        // 先查询是否存在
        MovieDTO movieDTO = baseMapper.selectByMovieMd5(fileInfoParam.getMovieMd5());

        if (ObjectUtil.isNull(movieDTO))
            return null;

        // 存在并且没合并和缓存不存在的时候就执行缓存
        if (!movieDTO.getIsMerge() && ObjectUtil.isNull(redisCacheUtil.getCacheMapValue("movieNoMerge", movieDTO.getMovieId().toString())))
            redisCacheUtil.setCacheMapValue("movieNoMerge", movieDTO.getMovieId().toString(), JSONObject.toJSONString(movieDTO));
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
        if (baseMapper.updateMergeChunksByMovieId(movieId) > 0) {
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
            MovieDTO movieDTO = baseMapper.selectByMovieMd5(movieMd5);
            // 如果刚好是播放状态并且已经合并，则不执行删除
            if (!movieDTO.getIsPlayer() && movieDTO.getIsMerge()) return false;
            // 根据id执行删除
            baseMapper.deleteById(movieId);
            movieChunkMapper.deleteByMovieId(movieId);
            redisCacheUtil.delCacheMapValue("movieNoMerge", movieId.toString());
            // 删除所有的临时分片文件
            fileOperationUtils.removeSliceFiles(movieMd5, totalIndex);
            // 删除单个文件
            fileOperationUtils.removeFile(movieMd5 + "." + movieDTO.getFileType());
            return true;
        } catch (Exception e) {
            log.error("Error during deleting movie: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public MovieDTO selectByMovieMd5(String movieMd5) {
        return baseMapper.selectByMovieMd5(movieMd5);
    }

    @Override
    public void updateIsPlayerByMovieMd5(String movieMd5) {
        baseMapper.updateIsPlayerByMovieMd5(movieMd5);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteMergeMovie(String movieMd5) {
        MovieDTO movieDTO = baseMapper.selectByMovieMd5(movieMd5);
        if (movieDTO != null) {
            // 暂时先只能删除可以播放的文件
            if (!movieDTO.getIsPlayer()) return false;
            Long movieId = movieDTO.getMovieId();
            // 根据id执行删除
            baseMapper.deleteById(movieId);
            // 删除单个文件
            fileOperationUtils.removeFile(movieMd5 + "." + movieDTO.getFileType());
            // 删除文件夹和文件
            fileOperationUtils.deleteMovieSlicesFromMinio(movieMd5);
        }
        return true;
    }

    @Override
    public MoviePO selectById(Long movieId) {
        return baseMapper.selectById(movieId);
    }

    @Override
    public MovieParam searchMovieParamById(Long movieId) {
        MovieParam movieParam = baseMapper.selectMovieParamById(movieId);
        String mainPoster = movieParam.getMainPoster();
        String poster = movieParam.getPoster();
        movieParam.setMainPoster(imageUtils.generatePresignedGetObjectUrl(minioConfig.getBucketNameMovieMainPoster(), movieParam.getMovieMd5().concat("/" + mainPoster)));
        movieParam.setPoster(imageUtils.generatePresignedGetObjectUrl(minioConfig.getBucketNameMoviePoster(), movieParam.getMovieMd5().concat("/" + poster)));
        return movieParam;
    }

    @Override
    public Boolean updateMovieMainPoster(String posterFileName, Long movieId) {
        return baseMapper.updateMainPosterById(posterFileName, movieId) > 0;
    }

    @Override
    public Boolean updateMoviePoster(String posterFileName, Long movieId) {
        return baseMapper.updatePosterById(posterFileName, movieId) > 0;
    }

    @Override
    public boolean updateMovieGroup(MovieGroupParam movieGroupParam) {
        return baseMapper.updateMovieGroup(movieGroupParam.getStatus(), movieGroupParam.getChannelId(), movieGroupParam.getMovieIdList()) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMovie(MovieParam movieParam) {
        try {
            List<Long> genreIdList = movieParam.getGenreIdList();
            // 先去重
            movieParam.setGenreIdList(genreIdList.stream().distinct().collect(Collectors.toList()));

            // 删除原有关联
            int deletedCount = genreMapper.deleteGenreIdListByMovieId(movieParam.getGenreIdList(), movieParam.getMovieId());
            if (deletedCount < 0) {
                log.error("删除电影类型关联失败");
                return false;
            }

            // 新增新的关联
            int insertedCount = genreMapper.insertGenreIdListByMovieId(movieParam.getGenreIdList(), movieParam.getMovieId());
            if (insertedCount < 0) {
                log.error("新增电影类型关联失败");
                return false;
            }

            // 更新电影基本信息
            int updatedRows = baseMapper.updateMovie(movieParam.getMovieName(), movieParam.getStatus(), movieParam.getChannelId(), movieParam.getMovieId());
            if (updatedRows <= 0) {
                log.error("电影信息更新失败，没有行受到影响");
                return false;
            }

            return true; // 所有操作成功，返回true
        } catch (Exception e) {
            log.error("更新电影信息过程中发生异常: {}", e.getMessage(), e);
            return false; // 捕获到任何异常也返回false
        }
    }

}
