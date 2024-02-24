package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.UmsMenuParamDto;
import com.weizi.common.domain.entity.UmsMenuEntity;
import com.weizi.common.domain.vo.RouterVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUmsMenuService extends IService<UmsMenuEntity> {
    List<RouterVO> searchSelfMenu();

    List<RouterVO> selectMenuList();

    WeiZiPageResult<UmsMenuEntity> selectList(UmsMenuParamDto menuParamDto);

    UmsMenuEntity searchMenuById(Long menuId);

    WeiZiResult saveMenu(UmsMenuEntity umsMenuEntity);

    WeiZiResult deleteMenusByMenuId(List<Long> menuIds);

    WeiZiResult updateMenu(UmsMenuEntity umsMenuEntity);
}
