package com.liubs.hotseconds.extension.transform.demo;

import java.lang.reflect.Field;

public class Boy {
    public static String name = "Tom";

    public static String printAll() {
        StringBuilder stringBuilder = new StringBuilder("{");
        for(Field field : Boy.class.getDeclaredFields()) {
            try {
                stringBuilder.append(field.getName()+":"+field.get(null)).append(",");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
