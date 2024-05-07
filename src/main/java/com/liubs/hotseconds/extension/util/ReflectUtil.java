package com.liubs.hotseconds.extension.util;

import com.liubs.hotseconds.extension.logging.Logger;

import java.lang.reflect.Field;

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
            declaredField.setAccessible(true);
            F result = (F)declaredField.get(obj);
            declaredField.setAccessible(accessible);
            return result;
        }catch (Exception e) {
            logger.error("ERROR in getField, obj={},fieldName={}",obj,fieldName);
        }

        return null;
    }
    public static<F,T> F getStaticField(Class<T> kclass, String fieldName)  {
        try{
            Field declaredField = kclass.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            F result = (F)declaredField.get(null);
            return result;
        }catch (Exception e) {
            logger.error("ERROR in getField, kclass={},fieldName={}",kclass,fieldName);
        }
        return null;
    }
    public static<F,T> F getStaticField(String kclass, String fieldName)  {
        try {
            return getStaticField(Class.forName(kclass),fieldName);
        } catch (Exception e) {
            logger.error("ERROR in getField, kclass={},fieldName={}",kclass,fieldName);
        }
        return null;
    }

}
