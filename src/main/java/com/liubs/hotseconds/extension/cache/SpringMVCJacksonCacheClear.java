package com.liubs.hotseconds.extension.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializerCache;
import com.fasterxml.jackson.databind.ser.SerializerCache;
import com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap;
import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.util.HeapInstancesHolder;
import org.hotswap.agent.util.ReflectionHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Liubsyy
 * @date 2024/1/1
 **/
public class SpringMVCJacksonCacheClear implements IHotExtHandler {
    private static Logger logger = Logger.getLogger(SpringMVCJacksonCacheClear.class);

    private List<ObjectMapper> objectMappers;
    private List<SerializerCache> serializerCaches;
    private List<DeserializerCache> deserializerCaches;
    private List<ReadOnlyClassToSerializerMap> classToSerializerMaps;

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {

        if(null == classToSerializerMaps || classToSerializerMaps.isEmpty()) {
            classToSerializerMaps = HeapInstancesHolder.getInstancesCache(ReadOnlyClassToSerializerMap.class);
        }

        if(null == objectMappers || objectMappers.isEmpty()) {
            objectMappers = HeapInstancesHolder.getInstancesCache(ObjectMapper.class);
        }
        if(null == serializerCaches || serializerCaches.isEmpty()) {
            serializerCaches = HeapInstancesHolder.getInstancesCache(SerializerCache.class);
        }
        if(null == deserializerCaches || deserializerCaches.isEmpty()) {
            deserializerCaches = HeapInstancesHolder.getInstancesCache(DeserializerCache.class);
        }

        try{
            classToSerializerMaps.forEach(c->{
                Object[] _buckets = (Object [])ReflectionHelper.get(c, "_buckets");
                Arrays.fill(_buckets, null);
            });

            objectMappers.forEach(c->{
                Map _rootDeserializers = (Map)ReflectionHelper.get(c, "_rootDeserializers");
                _rootDeserializers.clear();
            });
            serializerCaches.forEach(c->{
                c.flush();
            });

            deserializerCaches.forEach(c->{
                c.flushCachedDeserializers();
            });


        }catch (Exception e) {
            logger.error("cache clear err",e);
        }


    }
}
