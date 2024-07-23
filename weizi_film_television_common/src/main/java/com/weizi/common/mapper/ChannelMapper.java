package com.weizi.common.mapper;

import com.weizi.common.domain.po.ChannelPO;
import com.weizi.common.domain.vo.list.ChannelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChannelMapper extends BaseMapperX<ChannelPO> {
    List<ChannelVO> getChannelList();

    List<ChannelPO> selectAllChannels();

    List<ChannelVO> getHotTags();
}
