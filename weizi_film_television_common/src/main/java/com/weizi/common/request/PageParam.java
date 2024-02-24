package com.weizi.common.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @date AWei
 * @date 2024/02/12
 */
@Data
public class PageParam implements Serializable {

    // 当前页
    private Long pageNum = 1L;
    // 每页的数据量
    private Long pageSize = 10L;
}
