package com.itheima.reggie.common;

/**
 * @author 尛猫
 * @version 1.0
 * @description: 自定义异常类
 * @date 2022/8/10 22:00
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }

}
