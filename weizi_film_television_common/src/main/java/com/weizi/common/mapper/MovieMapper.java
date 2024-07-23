package com.weizi.common.mapper;

import com.weizi.common.domain.dto.MovieDTO;
import com.weizi.common.domain.dto.dataParam.MovieGroupParam;
import com.weizi.common.domain.dto.dataParam.MovieParam;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.domain.vo.list.ChannelVO;
import com.weizi.common.domain.vo.list.MovieVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper extends BaseMapperX<MoviePO> {

    int countTotal(@Param("movieName") String movieName);

    List<MovieDTO> selectMoviePage(@Param("movieName") String movieName, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);

    MovieDTO selectByMovieMd5(@Param("movieMd5") String movieMd5);

    int updateMergeChunksByMovieId(@Param("movieId") Long movieId);

    void updateIsPlayerByMovieMd5(@Param("movieMd5") String movieMd5);

    int updateMainPosterById(@Param("mainPoster") String mainPoster, @Param("movieId") Long movieId);

    int updatePosterById(@Param("poster") String poster, @Param("movieId") Long movieId);

    int updateMovie(@Param("movieName") String movieName, @Param("status") Boolean status, @Param("channelId") Long channelId, @Param("movieId") Long movieId);

    MovieParam selectMovieParamById(@Param("movieId") Long movieId);

    int updateMovieGroup(@Param("status") Boolean status, @Param("channelId") Long channelId, @Param("movieIdList") List<Long> movieIdList);

    List<MovieVO> getMovieChannelList(@Param("channelId") Long channelId, @Param("pageNum") Long pageNum, @Param("pageSize") Long pageSize);
}
