package com.weizi.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weizi.common.domain.dto.pageParam.MenuParamDTO;
import com.weizi.common.domain.po.MenuPO;
import com.weizi.common.domain.vo.RouterVO;
import com.weizi.common.response.WeiZiPageResult;
import com.weizi.common.response.WeiZiResult;

import java.util.List;

public interface MenuService extends IService<MenuPO> {
    List<RouterVO> searchSelfMenu();

    List<RouterVO> getSelfMenu(Boolean isSuperAdmin, Long adminId);

    List<RouterVO> selectMenuList();

    WeiZiPageResult<MenuPO> selectList(MenuParamDTO menuParamDto);

    MenuPO searchMenuById(Long menuId);

    WeiZiResult saveMenu(MenuPO menuPO);

    WeiZiResult deleteMenusByMenuId(Long menuId);

    WeiZiResult updateMenu(MenuPO menuPO);
}
