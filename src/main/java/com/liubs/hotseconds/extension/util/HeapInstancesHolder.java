package com.liubs.hotseconds.extension.util;

import com.liubs.findinstances.jvmti.InstancesOfClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对扫描堆内存的实例引用进行缓存
 * @author Liubsyy
 * @date 2024/5/7
 **/
public class HeapInstancesHolder {
    private static final Map<Class<?>, List<?>> instanceHolder = new HashMap<>();


    /**
     * 从缓存中取出实例，适用于实例不会更改的场景
     * @param klass
     * @return
     * @param <T>
     */
    public static <T> List<T> getInstancesCache(Class<T> klass) {
        List<?> instanceList = instanceHolder.get(klass);
        if(null == instanceList) {
            synchronized (instanceHolder) {
                instanceList = instanceHolder.get(klass);
                if(null == instanceList) {
                    instanceList = InstancesOfClass.getInstanceList(klass);
                    instanceHolder.put(klass,instanceList);
                }
            }
        }

        return (List<T>) instanceList;
    }
}
