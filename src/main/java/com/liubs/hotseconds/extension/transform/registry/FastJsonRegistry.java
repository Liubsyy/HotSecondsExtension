package com.liubs.hotseconds.extension.transform.registry;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.cache.FastJson2CacheClear;
import com.liubs.hotseconds.extension.cache.FastJsonCacheClear;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;

/**
 * 包括fastjson和fastjson2
 * @author Liubsyy
 * @date 2024/5/8
 **/
@ClassTransform
public class FastJsonRegistry {

    @OnClassLoad(className = "com.alibaba.fastjson.serializer.SerializeConfig")
    public static void registerFastJson() {
        AllExtensionsManager.getInstance().addHotExtHandler(new FastJsonCacheClear());
    }

    @OnClassLoad(className = "com.alibaba.fastjson2.JSONFactory")
    public static void registerFastJson2(){
        AllExtensionsManager.getInstance().addHotExtHandler(new FastJson2CacheClear());
    }
}
