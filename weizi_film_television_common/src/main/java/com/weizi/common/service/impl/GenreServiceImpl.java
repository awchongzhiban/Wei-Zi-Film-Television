package com.weizi.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weizi.common.domain.po.GenrePO;
import com.weizi.common.mapper.GenreMapper;
import com.weizi.common.service.GenreService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class GenreServiceImpl extends ServiceImpl<GenreMapper, GenrePO> implements GenreService {
    @Override
    public Boolean isNonExistentGenreId(List<Long> genreIds) {
        List<Long> allGenreIds = baseMapper.getAllGenreId();
        return new HashSet<>(allGenreIds).containsAll(genreIds);
    }
}
