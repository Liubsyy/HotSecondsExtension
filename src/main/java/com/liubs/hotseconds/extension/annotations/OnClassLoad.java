package com.liubs.hotseconds.extension.annotations;

import java.lang.annotation.*;


/**
 * @author Liubsyy
 * 当类加载的时候会调用加上这个注解的函数
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnClassLoad {
    String className();
}
