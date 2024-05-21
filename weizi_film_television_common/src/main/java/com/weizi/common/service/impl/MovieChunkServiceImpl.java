package com.weizi.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.domain.dto.MovieChunkDTO;
import com.weizi.common.domain.po.MovieChunkPO;
import com.weizi.common.mapper.MovieChunkMapper;
import com.weizi.common.service.MovieChunkService;
import com.weizi.common.utils.redis.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MovieChunkServiceImpl extends ServiceImpl<MovieChunkMapper, MovieChunkPO> implements MovieChunkService {

    private final MovieChunkMapper movieChunkMapper;
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    public MovieChunkServiceImpl(MovieChunkMapper movieChunkMapper) {
        this.movieChunkMapper = movieChunkMapper;
    }

    @Override
    public MovieChunkPO selectByMovieId(Long movieId) {
        return movieChunkMapper.selectByMovieId(movieId);
    }

    @Override
    public boolean updateByMovieId(MovieChunkPO movieChunk) {
        return movieChunkMapper.updateByMovieId(movieChunk) > 0;
    }
}
