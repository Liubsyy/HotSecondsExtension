package com.liubs.hotseconds.extension.cache;

import com.liubs.hotseconds.extension.IHotExtHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * velocity template cache clear, just clear html, css and js have no cache.
 */
public class VelocityHtmlCacheClear implements IHotExtHandler {
    @Override
    public void preHandle(ClassLoader classLoader, String path, byte[] content) {
    }

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {
        if(!path.endsWith(".html")) {
            return;
        }

        try{
            String fileName = path.substring(path.lastIndexOf("/")+1);

            Class<?> runtimeSingleton = Class.forName("org.apache.velocity.runtime.RuntimeSingleton");
            Field riField = runtimeSingleton.getDeclaredField("ri");
            riField.setAccessible(true);
            Object ri =  riField.get(null);
            riField.setAccessible(false);
            Field resourceManagerField = ri.getClass().getDeclaredField("resourceManager");
            resourceManagerField.setAccessible(true);
            Object resourceManager = resourceManagerField.get(ri);
            resourceManagerField.setAccessible(false);
            Field globalCacheField = resourceManager.getClass().getDeclaredField("globalCache");
            globalCacheField.setAccessible(true);
            Object resourceCache = globalCacheField.get(resourceManager);
            globalCacheField.setAccessible(false);

            Method enumerateKeys = resourceCache.getClass().getDeclaredMethod("enumerateKeys");
            enumerateKeys.setAccessible(true);
            Iterator cacheIterator = (Iterator)enumerateKeys.invoke(resourceCache);
            enumerateKeys.setAccessible(false);

            while(cacheIterator.hasNext()) {
                Object cacheKey = cacheIterator.next();
                if(cacheKey.toString().contains(fileName)) {
                    cacheIterator.remove();
                }
            }
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }
}