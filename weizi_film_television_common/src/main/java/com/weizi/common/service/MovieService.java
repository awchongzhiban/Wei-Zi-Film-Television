package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.MovieDTO;
import com.weizi.common.domain.dto.dataParam.FileInfoParam;
import com.weizi.common.domain.dto.dataParam.MovieParam;
import com.weizi.common.domain.dto.pageParam.MovieParamDTO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.domain.vo.list.MovieVO;
import com.weizi.common.response.WeiZiPageResult;

import java.util.Map;

public interface MovieService extends IService<MoviePO> {
    // 分页查询电影列表
    WeiZiPageResult<MovieVO> selectList(MovieParamDTO movieParamDTO);

    // 通过文件名称和类型检查电影是否存在
    MovieDTO checkMovieExist(FileInfoParam fileInfoParam);

    Long saveMovie(FileInfoParam fileInfoParam);

    boolean removeMovieCache(Long movieId);

    boolean deleteMovie(Long movieId, String movieMd5, int totalIndex);

    MovieDTO selectByMovieMd5(String movieMd5);

    void updateIsPlayerByMovieMd5(String movieMd5);

    boolean deleteMergeMovie(String movieMd5);
    
    MoviePO selectById(Long movieId);

    Boolean updateMoviePoster(String imageFileName, Long movieId);

    boolean updateMovie(MovieParam movieParam);

    MovieParam searchMovieParamById(Long movieId);

    Map<Long, String> selectAllGenres();
}
