package com.liubs.hotseconds.extension.cache;

import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.util.CoolDownRefresh;

/**
 * fastjson2缓存清理
 * @author Liubsyy
 * @date 2024/5/8
 **/
public class FastJson2CacheClear implements IHotExtHandler {


    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {

        if(CoolDownRefresh.INSTANCE.addCoolDown(JSONFactory.class, 3)) {
            ObjectWriterProvider writerProvider = JSONFactory.getDefaultObjectWriterProvider();
            writerProvider.cleanup(classLoader);

            ObjectReaderProvider readerProvider = JSONFactory.getDefaultObjectReaderProvider();
            readerProvider.cleanup(classLoader);
        }

    }
}
