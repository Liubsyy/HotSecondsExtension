package com.liubs.hotseconds.extension.cache;

import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.transform.registry.ModelMapperRegistry;
import com.liubs.hotseconds.extension.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

/**
 * modelmapper 热部署支持
 * @author Liubsyy
 * @date 2024/5/7
 */
public class ModelMapperCacheClear implements IHotExtHandler {
    private static Logger logger = Logger.getLogger(ModelMapperRegistry.class);

    //以下缓存在源码里都是ConcurrentHashMap

    //TypeInfoRegistry.cache
    private volatile Map cache = null;

    //PropertyInfoRegistry里的字段
    private Map MUTATOR_CACHE = null;
    private Map ACCESSOR_CACHE = null;
    private Map FIELD_CACHE = null;

    private Field typeInfoKeyField = null;

    /**
     * 移除modelmapper的缓存：
     * org.modelmapper.internal.TypeInfoRegistry.cache
     * org.modelmapper.internal.PropertyInfoRegistry.MUTATOR_CACHE
     * org.modelmapper.internal.PropertyInfoRegistry.ACCESSOR_CACHE
     * org.modelmapper.internal.PropertyInfoRegistry.FIELD_CACHE
     */
    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> reloadClass, String path, byte[] content) {
        if(null == reloadClass) {
            return;
        }
        try {
            if(null == cache) {
                synchronized (ModelMapperRegistry.class) {
                    if(null == cache) {
                        cache = ReflectUtil.getStaticField("org.modelmapper.internal.TypeInfoRegistry", "cache");
                        MUTATOR_CACHE = ReflectUtil.getStaticField("org.modelmapper.internal.PropertyInfoRegistry", "MUTATOR_CACHE");
                        ACCESSOR_CACHE = ReflectUtil.getStaticField("org.modelmapper.internal.PropertyInfoRegistry", "ACCESSOR_CACHE");
                        FIELD_CACHE = ReflectUtil.getStaticField("org.modelmapper.internal.PropertyInfoRegistry", "FIELD_CACHE");

                        typeInfoKeyField = Class.forName("org.modelmapper.internal.TypeInfoRegistry$TypeInfoKey").getDeclaredField("type");
                        typeInfoKeyField.setAccessible(true);
                    }
                }
            }
            if(null == cache || null == typeInfoKeyField) {
                return;
            }

            Iterator iterator = cache.keySet().iterator();
            boolean needClear = false;
            while(iterator.hasNext()) {
                Object element = iterator.next();
                //热更新的类如果有缓存，则全部清除(有依赖关系)
                if(reloadClass == typeInfoKeyField.get(element)){
                    needClear = true;
                    break;
                }
            }

            if(needClear) {
                cache.clear();
                MUTATOR_CACHE.clear();
                ACCESSOR_CACHE.clear();
                FIELD_CACHE.clear();
            }

        } catch (Exception e) {
            logger.error("Refresh TypeInfoRegistry err",e);
        }
    }

}
