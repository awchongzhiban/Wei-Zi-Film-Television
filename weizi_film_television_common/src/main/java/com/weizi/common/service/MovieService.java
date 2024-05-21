package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.dataParam.FileInfoParam;
import com.weizi.common.domain.po.MoviePO;
import com.weizi.common.response.WeiZiResult;
import org.springframework.web.multipart.MultipartFile;

public interface MovieService extends IService<MoviePO> {
    // 通过文件名称和类型检查电影是否存在（在当前管理员ID下）
    MoviePO checkMovieExist(FileInfoParam fileInfoParam);

    Long saveMovie(FileInfoParam fileInfoParam);

    boolean removeMovieCache(Long movieId);

    boolean deleteMovie(Long movieId, String movieMd5, int totalIndex);

    MoviePO selectByMovieMd5(String movieMd5);
}
