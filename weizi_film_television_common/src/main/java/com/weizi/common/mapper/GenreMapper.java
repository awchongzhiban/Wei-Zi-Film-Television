package com.weizi.common.mapper;

import com.weizi.common.domain.dto.MovieToGenreDTO;
import com.weizi.common.domain.po.GenrePO;
import com.weizi.common.domain.po.MovieChunkPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GenreMapper extends BaseMapperX<GenrePO> {
    List<MovieToGenreDTO> selectGenreIdAndNameByMovieIds(@Param("movieIds") List<Long> movieIds);

    List<GenrePO> selectAllGenres();

    List<Long> getAllGenreId();

    int deleteGenreIdListByMovieId(@Param("genreIds") List<Long> genreIdList, @Param("movieId") Long movieId);

    int insertGenreIdListByMovieId(@Param("genreIds") List<Long> genreIdList, @Param("movieId") Long movieId);
}
