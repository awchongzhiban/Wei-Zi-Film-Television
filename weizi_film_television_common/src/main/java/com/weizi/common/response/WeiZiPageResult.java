package com.weizi.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @date AWei
 * @date 2024/02/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeiZiPageResult<T> implements Serializable {

    private List<T> list;
    private long total;


}
