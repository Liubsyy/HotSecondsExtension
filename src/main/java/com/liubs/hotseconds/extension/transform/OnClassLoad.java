package com.liubs.hotseconds.extension.transform;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnClassLoad {
    String className();
}
