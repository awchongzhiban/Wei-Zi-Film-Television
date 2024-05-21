package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.MovieChunkDTO;
import com.weizi.common.domain.po.MovieChunkPO;
import org.apache.ibatis.annotations.Param;

public interface MovieChunkService extends IService<MovieChunkPO> {
    MovieChunkPO selectByMovieId(@Param("movieId") Long movieId);

    boolean updateByMovieId(MovieChunkPO movieChunk);
}
