package com.weizi.support.config.mybatisplus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 新增时调用
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createTime", () -> now, LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateTime", () -> now, LocalDateTime.class);
    }

    /**
     * 修改时调用
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        LocalDateTime now = LocalDateTime.now();
        this.strictUpdateFill(metaObject, "updateTime", () -> now, LocalDateTime.class);
    }
}
