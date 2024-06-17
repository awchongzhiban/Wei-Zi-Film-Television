package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.po.GenrePO;

import java.util.List;

public interface GenreService extends IService<GenrePO> {
    Boolean isNonExistentGenreId(List<Long> genreIds);
}
