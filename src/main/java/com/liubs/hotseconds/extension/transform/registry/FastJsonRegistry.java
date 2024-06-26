package com.liubs.hotseconds.extension.transform.registry;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.cache.FastJson2CacheClear;
import com.liubs.hotseconds.extension.cache.FastJsonCacheClear;
import com.liubs.hotseconds.extension.holder.InstancesHolder;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;
import javassist.CtClass;

/**
 * 包括fastjson和fastjson2
 * @author Liubsyy
 * @date 2024/5/8
 **/
@ClassTransform
public class FastJsonRegistry {

    @OnClassLoad(className = "com.alibaba.fastjson.serializer.SerializeConfig")
    public static void registerFastJsonSerializeConfig(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new FastJsonCacheClear());
    }
    @OnClassLoad(className = "com.alibaba.fastjson.parser.ParserConfig")
    public static void registerFastJsonParserConfig(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new FastJsonCacheClear());
    }

    @OnClassLoad(className = "com.alibaba.fastjson2.writer.ObjectWriterProvider")
    public static void registerFastJson2ObjectWriterProvider(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new FastJson2CacheClear());
    }
    @OnClassLoad(className = "com.alibaba.fastjson2.reader.ObjectReaderProvider")
    public static void registerFastJson2ObjectReaderProvider(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructor(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new FastJson2CacheClear());
    }

}
