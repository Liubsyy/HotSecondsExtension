package com.liubs.hotseconds.extension.cache;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.holder.InstancesHolder;
import com.liubs.hotseconds.extension.holder.RefreshCoolDown;

/**
 * fastjson缓存清理
 * @author Liubsyy
 * @date 2024/5/8
 **/
public class FastJsonCacheClear implements IHotExtHandler {

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {
        if(null == classz) {
            return;
        }

        //fastjson1序列化缓存清除
        if(RefreshCoolDown.INSTANCE.addCoolDown(SerializeConfig.class, 3)) {
            InstancesHolder.getInstances(SerializeConfig.class).forEach(SerializeConfig::clearSerializers);
        }

        //fastjson1清理反序列化缓存
        if(RefreshCoolDown.INSTANCE.addCoolDown(ParserConfig.class, 3)) {
            InstancesHolder.getInstances(ParserConfig.class).forEach(ParserConfig::clearDeserializers);
        }

    }
}
