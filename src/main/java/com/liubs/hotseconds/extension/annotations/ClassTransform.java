package com.liubs.hotseconds.extension.annotations;

import java.lang.annotation.*;


/**
 * @author Liubsyy
 * com.liubs.hotseconds.extension.transform 目录下的类加上这个注解，会扫描该类的方法上的OnClassLoad注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClassTransform {
}
