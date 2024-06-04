package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.MovieDTO;
import com.weizi.common.domain.dto.dataParam.FileInfoParam;
import com.weizi.common.domain.dto.pageParam.MovieParamDTO;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.domain.vo.list.MovieVO;
import com.weizi.common.response.WeiZiPageResult;

public interface MovieService extends IService<MoviePO> {
    // 分页查询电影列表
    WeiZiPageResult<MovieVO> selectList(MovieParamDTO movieParamDto);

    // 通过文件名称和类型检查电影是否存在
    MovieDTO checkMovieExist(FileInfoParam fileInfoParam);

    Long saveMovie(FileInfoParam fileInfoParam);

    boolean removeMovieCache(Long movieId);

    boolean deleteMovie(Long movieId, String movieMd5, int totalIndex);

    MovieDTO selectByMovieMd5(String movieMd5);

    void updateIsPlayerByMovieMd5(String movieMd5);

    boolean deleteMergeMovie(String movieMd5);

    String getMovieUrl(String movieMd5);
}
