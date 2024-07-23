package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.pageParam.ChannelParamDTO;
import com.weizi.common.domain.po.ChannelPO;
import com.weizi.common.domain.vo.list.ChannelVO;
import com.weizi.common.domain.vo.list.MovieVO;

import java.util.List;

public interface ChannelService extends IService<ChannelPO> {
    
    List<ChannelVO> getChannelList();

    List<ChannelVO> getHotTags();

    List<MovieVO> getMovieChannelList(ChannelParamDTO channelParamDTO);
}
