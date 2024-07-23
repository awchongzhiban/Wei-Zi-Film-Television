package com.weizi.common.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.config.MinioConfig;
import com.weizi.common.domain.dto.pageParam.ChannelParamDTO;
import com.weizi.common.domain.po.ChannelPO;
import com.weizi.common.domain.vo.list.ChannelVO;
import com.weizi.common.domain.vo.list.MovieVO;
import com.weizi.common.mapper.ChannelMapper;
import com.weizi.common.mapper.MovieMapper;
import com.weizi.common.service.ChannelService;
import com.weizi.common.utils.imageutils.ImageUtils;
import com.weizi.common.utils.redis.RedisCacheUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, ChannelPO> implements ChannelService {
    private final MinioConfig minioConfig;
    private final ImageUtils imageUtils;
    private final RedisCacheUtil redisCacheUtil;
    private final MovieMapper movieMapper;

    public ChannelServiceImpl(MinioConfig minioConfig, ImageUtils imageUtils, RedisCacheUtil redisCacheUtil, MovieMapper movieMapper) {
        this.minioConfig = minioConfig;
        this.imageUtils = imageUtils;
        this.redisCacheUtil = redisCacheUtil;
        this.movieMapper = movieMapper;
    }

    @Override
    public List<ChannelVO> getChannelList() {
        // 先查询redis缓存
        List<ChannelVO> channelVOListCache = redisCacheUtil.getCacheList("channelList");
        // 如果缓存为空，则查询数据库
        if (ObjectUtil.isEmpty(channelVOListCache)) {
            List<ChannelVO> channelVOListDB = baseMapper.getChannelList();
            // 缓存数据
            redisCacheUtil.setCacheList("channelList", channelVOListDB);
            return channelVOListDB;
        }
        return channelVOListCache;
    }

    @Override
    public List<ChannelVO> getHotTags() {
        // 先查询redis缓存
        List<ChannelVO> channelVOListCache = redisCacheUtil.getCacheList("channelHotTags");
        if (ObjectUtil.isEmpty(channelVOListCache)) {
            List<ChannelVO> channelVOListDB = baseMapper.getHotTags();
            // 缓存数据
            redisCacheUtil.setCacheList("channelHotTags", channelVOListDB);
            return channelVOListDB;
        }
        return channelVOListCache;
    }

    @Override
    public List<MovieVO> getMovieChannelList(ChannelParamDTO channelParamDTO) {
        // 计算分页查询的偏移量
        Long pageNum = (channelParamDTO.getPageNum() - 1) * channelParamDTO.getPageSize();
        List<MovieVO> movieChannelList = movieMapper.getMovieChannelList(channelParamDTO.getChannelId(), pageNum, channelParamDTO.getPageSize());
        if (ObjectUtil.isEmpty(movieChannelList)) return null;
        movieChannelList.forEach(movieVO -> {
            movieVO.setMainPoster(imageUtils.generatePresignedGetObjectUrl(minioConfig.getBucketNameMovieMainPoster(), movieVO.getMovieMd5().concat("/" + movieVO.getMainPoster())));
        });
        return movieChannelList;
    }
}
