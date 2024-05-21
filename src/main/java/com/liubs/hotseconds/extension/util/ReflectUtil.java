package com.liubs.hotseconds.extension.util;

import com.liubs.hotseconds.extension.logging.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Liubsyy
 * @date 2023/7/9 11:58 PM
 **/
public class ReflectUtil {
    private static Logger logger = Logger.getLogger(ReflectUtil.class);


    public static Object get(Object target, String fieldName) {
        if (target == null)
            throw new NullPointerException("Target object cannot be null.");

        Class<?> clazz = target.getClass();

        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                break;
            } catch (NoSuchFieldException e) {
                // ignore
            }
            clazz = clazz.getSuperclass();
        }

        if (clazz == null) {
            throw new IllegalArgumentException(String.format("No such field %s.%s on %s", target.getClass(), fieldName, target));
        }

        return get(target, clazz, fieldName);
    }
    public static Object get(Object target, Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(String.format("No such field %s.%s on %s", clazz.getName(), fieldName, target), e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Illegal access field %s.%s on %s", clazz.getName(), fieldName, target), e);
        }
    }

    public static Object invoke(Object target, String methodName) {
        return invoke(target, target.getClass(), methodName, new Class[] {});
    }

    public static Object invoke(Object target, Class<?> clazz, String methodName, Class<?>[] parameterTypes,
                                Object... args) {
        try {
            Method method = null;
            try {
                method = clazz.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
            }
            method.setAccessible(true);

            return method.invoke(target, args);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format("Illegal arguments method %s.%s(%s) on %s, params %s", clazz.getName(), methodName,
                    Arrays.toString(parameterTypes), target, Arrays.toString(args)), e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(String.format("Error invoking method %s.%s(%s) on %s, params %s", clazz.getName(), methodName,
                    Arrays.toString(parameterTypes), target, Arrays.toString(args)), e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("No such method %s.%s(%s) on %s, params %s", clazz.getName(), methodName,
                    Arrays.toString(parameterTypes), target, Arrays.toString(args)), e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("No such method %s.%s(%s) on %s, params %s", clazz.getName(), methodName,
                    Arrays.toString(parameterTypes), target, Arrays.toString(args)), e);
        }
    }

    public static Object getNoException(Object target, Class<?> clazz, String fieldName) {
        try {
            return get(target, clazz, fieldName);
        } catch (Exception e) {
            logger.trace("Error getting field {}.{} on object {}", e, clazz, fieldName, target);
            return null;
        }
    }

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
