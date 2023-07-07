package com.liubs.hotseconds.extension.transform;

import java.lang.annotation.*;


/**
 * 加上这个注解会被扫描该类的OnClassLoad注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ClassTransform {
}
