package com.liubs.hotseconds.extension.transform.registry;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.cache.GsonCacheClear;
import com.liubs.hotseconds.extension.holder.InstancesHolder;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;
import org.hotswap.agent.javassist.CtClass;

/**
 * @author Liubsyy
 * @date 2024/5/8
 */
@ClassTransform
public class GsonRegistry {
    @OnClassLoad(className = "com.google.gson.Gson")
    public static void registerGson(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new GsonCacheClear());
    }
}
