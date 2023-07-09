package com.liubs.hotseconds.extension.util;

import com.liubs.hotseconds.extension.logging.Logger;
import org.hotswap.agent.util.spring.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Liubsyy
 * @date 2023/7/9 11:58 PM
 **/
public class ReflectUtil {
    private static Logger logger = Logger.getLogger(ReflectUtil.class);


    public static<F,T> F getField(T obj, String fieldName)  {
        try{
            Field declaredField = obj.getClass().getDeclaredField(fieldName);
            boolean accessible = declaredField.isAccessible();
            F result = (F)declaredField.get(obj);
            declaredField.setAccessible(accessible);
            return result;
        }catch (Exception e) {
            logger.error("ERROR in getField, obj={},fieldName={}",obj,fieldName);
        }

        return null;
    }


}
