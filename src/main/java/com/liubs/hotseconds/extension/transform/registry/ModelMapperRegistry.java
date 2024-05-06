package com.liubs.hotseconds.extension.transform.registry;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;
import com.liubs.hotseconds.extension.util.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

/**
 * modelmapper 热部署支持
 * @author Liubsyy
 * @date 2024/5/6
 */
@ClassTransform
public class ModelMapperRegistry {
    private static Logger logger = Logger.getLogger(ModelMapperRegistry.class);

    private static volatile Map cache = null;

    /**
     * 移除modelmapper的缓存： org.modelmapper.internal.TypeInfoRegistry.cache.type中的对应key
     */
    @OnClassLoad(className = "org.modelmapper.internal.TypeInfoRegistry")
    public static void registerTypeInfoRegistry() {
        AllExtensionsManager.getInstance().addHotExtHandler((classLoader, classz, path, content) -> {
            if(null == classz) {
                return;
            }
            try {
                if(null == cache) {
                    synchronized (ModelMapperRegistry.class) {
                        if(null == cache) {
                            Field cacheField = Class.forName("org.modelmapper.internal.TypeInfoRegistry").getDeclaredField("cache");
                            cacheField.setAccessible(true);
                            cache = ((Map) cacheField.get(null));
                        }
                    }
                }
                if(null == cache) {
                    return;
                }

                synchronized (cache) {
                    Iterator iterator = cache.keySet().iterator();
                    while(iterator.hasNext()) {
                        Object next = iterator.next();
                        if(classz == ReflectUtil.getField(next,"type")){
                            iterator.remove();
                            logger.info("remove cache {}",classz);
                        }
                    }
                }

            } catch (Exception e) {
                logger.error("Refresh TypeInfoRegistry err",e);
            }
        });
    }
}
