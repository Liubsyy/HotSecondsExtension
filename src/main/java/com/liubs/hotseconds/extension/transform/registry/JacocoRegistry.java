package com.liubs.hotseconds.extension.transform.registry;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.holder.InstancesHolder;
import com.liubs.hotseconds.extension.jacoco.JacocoTransform;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;
import javassist.CtClass;

/**
 * jacoco覆盖率支持
 * @author Liubsyy
 * @date 2024/6/5
 **/
@ClassTransform
public class JacocoRegistry {

    @OnClassLoad(className = "org.jacoco.agent.rt.RT")
    public static void registerGson(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new JacocoTransform());
    }
}
