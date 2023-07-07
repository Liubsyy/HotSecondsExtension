package com.liubs.hotseconds.extension.annotations;

import java.lang.annotation.*;


/**
 * @author Liubsyy
 * 这个注解没有实质性的作用，标记一下可远程执行的方法
 * 右键->Run method on remote server执行函数即可
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RemoteMethodTest {
    String value() default "";
}
