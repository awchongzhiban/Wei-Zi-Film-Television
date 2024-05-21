package com.weizi.common.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IdsStringToListTypeHandler extends BaseTypeHandler<List<Long>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        // 实现将 List<Long> 写入 PreparedStatement 的逻辑，通常用于插入或更新操作。此处示例省略，因查询场景无需此方法。
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String idsStr = rs.getString(columnName);
        return parseIds(idsStr);
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String idsStr = rs.getString(columnIndex);
        return parseIds(idsStr);
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String idsStr = cs.getString(columnIndex);
        return parseIds(idsStr);
    }

    private List<Long> parseIds(String idsStr) {
        if (idsStr == null || idsStr.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(idsStr.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());
    }
}
