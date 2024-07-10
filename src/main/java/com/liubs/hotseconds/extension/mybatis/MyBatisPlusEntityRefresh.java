package com.liubs.hotseconds.extension.mybatis;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.transform.mybatis.MyBatisClassPathMapperScannerPatch;
import com.liubs.hotseconds.extension.transform.mybatis.MyBatisSpringBeanDefinition;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


/**
 * 1、实体类增加字段后，mybatis字段缓存需要重新加载
 * 2、实体类字段增加注解后，mybatis字段缓存需要重新加载
 * @author afq
 * @data 2024/04/18
 */
public class MyBatisPlusEntityRefresh implements IHotExtHandler {
    private static Logger logger = Logger.getLogger(MyBatisClassPathMapperScannerPatch.class);

    /**
     * 判断是否是实体类，这种判断方式只是其中的一种。并不完善。
     * 后根据需求，再进一步补充。
     *
     * */
    public static boolean isEntity(Class<?> clazz) {
        // 检查类是否直接继承 Model
        if (Model.class.isAssignableFrom(clazz)) {
            return true;
        }

        // 检查类的父类是否继承 Model
        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            if (Model.class.isAssignableFrom(superClass)) {
                return true;
            }
            superClass = superClass.getSuperclass();
        }
        // 默认不是实体类
        return false;
    }

    public Configuration getConfiguration(ArrayList<Configuration> configurations){
        Configuration configuration = configurations.stream().filter(c->c.getClass() == MybatisConfiguration.class).findFirst().orElse(null);
        if(configuration != null){
           return configuration;
        }
        return configurations.get(0);
    }

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] bytes) {
        if(classz == null || classz.isInterface()) {
            return;
        }
        if(!isEntity(classz)) {
            return;
        }
        if(null == MyBatisSpringBeanDefinition.getMapperScanner()) {
            return;
        }
        try {
            /**
             * 参考 AbstractSqlInjector.inspectInject 方法
             *
             */
            Class<?> sqlSessionFactoryClz = Class.forName("org.apache.ibatis.session.defaults.DefaultSqlSessionFactory",true,classLoader);
            Field staticConfiguration = null;
            try{
                staticConfiguration = sqlSessionFactoryClz.getDeclaredField("_staticConfiguration");
            }catch (NoSuchFieldException ex) {
                return;
            }
            Configuration configuration =  getConfiguration((ArrayList<Configuration>)staticConfiguration.get(null));
            if(configuration instanceof MybatisConfiguration){
                MybatisConfiguration mybatisConfiguration = (MybatisConfiguration)configuration;
                Class mapperClass =  getMapperClass( mybatisConfiguration, classz);
                if(mapperClass == null){
                    return;
                }

                //移除自定义方法缓存
                Field mappedStatementsField = MybatisConfiguration.class.getDeclaredField("mappedStatements");
                mappedStatementsField.setAccessible(true);
                Map<String, MappedStatement> mappedStatementsAll = (Map<String, MappedStatement>) mappedStatementsField.get(mybatisConfiguration);
                final String typeKey = mapperClass.getName() + StringPool.DOT;
                Set<String> mapperSet = mappedStatementsAll.keySet().stream().filter(ms -> ms.startsWith(typeKey)).collect(Collectors.toSet());
                if (!mapperSet.isEmpty()) {
                    List<String> methodNames = Arrays.stream("updateById,insert,selectById".split(",")).collect(Collectors.toList());
                   Iterator it =  mapperSet.iterator();
                   while (it.hasNext()){
                       String key = (String) it.next();
                       String [] keys = key.split("\\.");
                       String methodName = keys[keys.length - 1];
                       if(methodNames.contains(methodName)){
                           mappedStatementsAll.remove(key);
                       }
                   }
                }
                
                //构建MapperBuilderAssistant
                String xmlResource = mapperClass.getName().replace(StringPool.DOT, StringPool.SLASH) + ".java (best guess)";
                MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(configuration, xmlResource);
                builderAssistant.setCurrentNamespace(mapperClass.getName());

                //移除实体类对应的字段缓存，否则在初始化TableInfo的时候，不重新初始化字段集合
                Field field = ReflectionKit.class.getDeclaredField("CLASS_FIELD_CACHE");
                field.setAccessible(true);
                Map<Class<?>, List<Field>> cache = (Map<Class<?>, List<Field>>) field.get(null);
                cache.remove(classz);

                //移除mapper缓存，否则不执行循环注入自定义方法  if (!mapperRegistryCache.contains(className)) {
                Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(builderAssistant.getConfiguration());
                String className = mapperClass.toString();
                mapperRegistryCache.remove(className);

                //移除实体类对应的表缓存，否则不重新初始化TableInfo
                TableInfoHelper.remove(classz);

                //移除实体类对应的映射器缓存
                DefaultReflectorFactory reflectorFactory = (DefaultReflectorFactory)mybatisConfiguration.getReflectorFactory();
                Field reflectorMapField = DefaultReflectorFactory.class.getDeclaredField("reflectorMap");
                reflectorMapField.setAccessible(true);
                ConcurrentMap<Class<?>, Reflector> reflectorMap = (ConcurrentMap<Class<?>, Reflector> ) reflectorMapField.get(reflectorFactory);
                reflectorMap.remove(classz);

                //注入自定义方法
                GlobalConfigUtils.getSqlInjector(configuration).inspectInject(builderAssistant, mapperClass);

            }

        } catch (Exception e) {
            logger.error("Refresh Mybatis Bean err",e);
        }
    }

    //获取Mapper class
    private Class getMapperClass(MybatisConfiguration mybatisConfiguration,Class classz) {
        Class mapperClass = null;
        MapperRegistry mapperRegistry = mybatisConfiguration.getMapperRegistry();
        Collection<Class<?>> mappers = mapperRegistry.getMappers();
        for (Class<?> mapper : mappers) {
            Type [] superClassTypeArray = mapper.getGenericInterfaces();
            for (int i = 0; i < superClassTypeArray.length; i++) {
                Type superClassType = superClassTypeArray[i];
                if(!(superClassType instanceof ParameterizedType)){
                    continue;
                }
                ParameterizedType parameterizedType = (ParameterizedType) superClassType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    Type modelType = Arrays.stream(actualTypeArguments).filter(type->type.getTypeName().equals(classz.getTypeName())).findAny().orElse(null);
                    if(modelType != null){
                        mapperClass = mapper;
                        break;
                    }
                }
            }
            if(mapperClass != null){
                break;
            }
        }
        return mapperClass;
    }

}
