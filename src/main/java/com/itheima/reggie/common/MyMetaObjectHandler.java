package com.itheima.reggie.common;/**
 * 包名称： com.itheima.reggie.common
 * 类名称：MyMetaObjectHandler
 * 类描述：
 * 创建人：@author xxx
 * 创建时间：2022/8/9/21:40
 */

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author 尛猫
 * @version 1.0
 * @description: 自定义元数据对象处理器
 * @date 2022/8/9 21:40
 */

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {


    /**
     * mp公共字段填充自动填充
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert...]");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getThreadLocal());
        metaObject.setValue("updateUser", BaseContext.getThreadLocal());


    }

    /**
     * mp公共字段更新自动填充
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充P[update...]");
        log.info("线程id=" + Thread.currentThread().getId());
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        System.out.println(BaseContext.getThreadLocal());
        metaObject.setValue("updateUser", BaseContext.getThreadLocal());
    }
}
