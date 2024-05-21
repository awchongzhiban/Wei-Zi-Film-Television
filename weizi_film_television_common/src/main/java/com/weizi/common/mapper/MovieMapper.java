package com.weizi.common.mapper;

import com.weizi.common.domain.dto.MovieDTO;
import com.weizi.common.domain.po.MoviePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MovieMapper extends BaseMapperX<MoviePO> {
    MoviePO selectByMovieMd5(@Param("movieMd5") String movieMd5);

    int updateMergeChunksByMovieId(@Param("movieId") Long movieId, @Param("adminId") Long adminId);
}
