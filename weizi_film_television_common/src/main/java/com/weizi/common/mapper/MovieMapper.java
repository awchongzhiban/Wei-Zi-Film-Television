package com.weizi.common.mapper;

import com.weizi.common.domain.dto.MovieDTO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.domain.vo.list.MovieVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper extends BaseMapperX<MoviePO> {

    int countTotal(@Param("movieName") String movieName);

    List<MoviePO> selectMoviePage(@Param("movieName") String movieName, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);

    MoviePO selectByMovieMd5(@Param("movieMd5") String movieMd5);

    int updateMergeChunksByMovieId(@Param("movieId") Long movieId, @Param("adminId") Long adminId);

    void updateIsPlayerByMovieMd5(@Param("movieMd5") String movieMd5);
}
