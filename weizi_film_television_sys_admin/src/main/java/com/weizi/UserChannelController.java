package com.weizi;

import com.weizi.common.domain.dto.pageParam.ChannelParamDTO;
import com.weizi.common.domain.vo.list.ChannelVO;
import com.weizi.common.domain.vo.list.MovieVO;
import com.weizi.common.response.WeiZiResult;
import com.weizi.common.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @date AWei
 * @date 2024/05/21
 */
@RestController
@RequestMapping("user/channel")
@Slf4j
public class UserChannelController {

    private final ChannelService channelService;

    public UserChannelController(ChannelService channelService) {
        this.channelService = channelService;
    }

    /**
     * 获取频道列表
     */
    @GetMapping("getChannelList")
    public WeiZiResult getChannelList() {
        List<ChannelVO> channelList = channelService.getChannelList();
        return WeiZiResult.success(channelList);
    }

    /**
     * 获取热门频道标签
     */
    @GetMapping("getHotTags")
    public WeiZiResult getHotTags() {
        List<ChannelVO> hotTags = channelService.getHotTags();
        return WeiZiResult.success(hotTags);
    }

    @GetMapping("getMovieChannelList")
    public WeiZiResult getMovieChannelList(ChannelParamDTO channelParamDTO) {
        return WeiZiResult.success(channelService.getMovieChannelList(channelParamDTO));
    }
}
