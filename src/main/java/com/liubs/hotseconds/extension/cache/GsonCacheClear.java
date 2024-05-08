package com.liubs.hotseconds.extension.cache;

import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.exception.RemoteItException;
import com.liubs.hotseconds.extension.holder.InstancesHolder;
import com.liubs.hotseconds.extension.holder.RefreshCoolDown;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * gson缓存清理
 * @author Liubsyy
 * @date 2024/5/8
 */
public class GsonCacheClear implements IHotExtHandler {

    private Field typeTokenCacheField = null;

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {
        if(null == classz) {
            return;
        }
        if(null == typeTokenCacheField) {
            synchronized (GsonCacheClear.class) {
                if(null == typeTokenCacheField) {
                    try {
                        typeTokenCacheField = Class.forName("com.google.gson.Gson").getDeclaredField("typeTokenCache");
                    } catch (NoSuchFieldException | ClassNotFoundException e) {
                        throw new RemoteItException(e);
                    }
                    typeTokenCacheField.setAccessible(true);
                }
            }
        }

        if(RefreshCoolDown.INSTANCE.addCoolDown(com.google.gson.Gson.class, 3)) {
            InstancesHolder.getInstances(com.google.gson.Gson.class).forEach(c->{
                try {
                    Map typeTokenCache = (Map) typeTokenCacheField.get(c);
                    typeTokenCache.clear();
                } catch (IllegalAccessException e) {
                    throw new RemoteItException(e);
                }
            });
        }
    }
}
