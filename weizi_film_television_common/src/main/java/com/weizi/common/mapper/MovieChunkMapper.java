package com.weizi.common.mapper;

import com.weizi.common.domain.po.MovieChunkPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MovieChunkMapper extends BaseMapperX<MovieChunkPO> {
    MovieChunkPO selectByMovieId(@Param("movieId") Long movieId);

    int updateByMovieId(MovieChunkPO movieChunk);

    void deleteByMovieId(@Param("movieId") Long movieId);
}
