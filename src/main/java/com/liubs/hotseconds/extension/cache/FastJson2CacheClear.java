package com.liubs.hotseconds.extension.cache;

import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.holder.InstancesHolder;
import com.liubs.hotseconds.extension.holder.RefreshCoolDown;

/**
 * fastjson2缓存清理
 * @author Liubsyy
 * @date 2024/5/8
 **/
public class FastJson2CacheClear implements IHotExtHandler {


    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {
        if(null == classz) {
            return;
        }

        if(RefreshCoolDown.INSTANCE.addCoolDown(JSONFactory.class, 3)) {
            InstancesHolder.getInstances(ObjectWriterProvider.class).forEach(c->{
                c.cleanup(classLoader);
            });

            InstancesHolder.getInstances(ObjectReaderProvider.class).forEach(c->{
                c.cleanup(classLoader);
            });
        }

    }
}
