package com.itheima.reggie.common;

/**
 * @author 尛猫
 * @version 1.0
 * @description: 理由线程传id
 * @date 2022/8/10 13:05
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static Long getThreadLocal() {
        return threadLocal.get();
    }

    public static void setThreadLocal(Long id) {
        threadLocal.set(id);
    }

}
