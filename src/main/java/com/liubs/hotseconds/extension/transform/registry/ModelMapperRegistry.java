package com.liubs.hotseconds.extension.transform.registry;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.cache.ModelMapperCacheClear;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;

/**
 * modelmapper 热部署支持
 * @author Liubsyy
 * @date 2024/5/6
 */
@ClassTransform
public class ModelMapperRegistry {


    @OnClassLoad(className = "org.modelmapper.internal.TypeInfoRegistry")
    public static void registerTypeInfoRegistry() {
        AllExtensionsManager.getInstance().addHotExtHandler(new ModelMapperCacheClear());
    }
}
