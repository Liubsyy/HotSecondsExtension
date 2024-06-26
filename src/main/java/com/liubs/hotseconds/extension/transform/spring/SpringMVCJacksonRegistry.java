package com.liubs.hotseconds.extension.transform.spring;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.cache.JacksonCacheClear;
import com.liubs.hotseconds.extension.holder.InstancesHolder;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;
import javassist.CtClass;

/**
 * @author Liubsyy
 * @date 2024/1/1
 **/
@ClassTransform
public class SpringMVCJacksonRegistry {

    private static Logger logger = Logger.getLogger(SpringMVCJacksonRegistry.class);

    @OnClassLoad(className = "com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap")
    public static void registryReadOnlyClassToSerializerMap(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new JacksonCacheClear());
    }

    @OnClassLoad(className = "com.fasterxml.jackson.databind.ObjectMapper")
    public static void registryObjectMapper(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new JacksonCacheClear());
    }

    @OnClassLoad(className = "com.fasterxml.jackson.databind.ser.SerializerCache")
    public static void registrySerializerCache(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new JacksonCacheClear());
    }

    @OnClassLoad(className = "com.fasterxml.jackson.databind.deser.DeserializerCache")
    public static void registryDeserializerCache(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new JacksonCacheClear());
    }


}
