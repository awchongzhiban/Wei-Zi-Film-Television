package com.weizi.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.weizi.common.request.PageParam;
import com.weizi.common.response.WeiZiPageResult;
import org.apache.ibatis.annotations.Param;

/**
 * 封装的Mapper接口
 */
public interface BaseMapperX<T>  extends BaseMapper<T> {
    /**
     * 分页封装
     * 参数1：分页条件
     * 参数2：wrapper：查询条件
     */
    default WeiZiPageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> wrapper){
        // 定义分页
        IPage<T> page = new Page<>();
        // 设置页码
        page.setCurrent(pageParam.getPageNum());
        page.setSize(pageParam.getPageSize());
        // 查询逻辑
        selectPage(page,wrapper);
        return new WeiZiPageResult<>(page.getRecords(),page.getTotal());
    }
}
