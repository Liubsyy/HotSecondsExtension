package com.liubs.hotseconds.extension.mybatis;


import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.holder.InstancesHolder;
import com.liubs.hotseconds.extension.logging.Logger;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * mybatis配置.xml修改完热部署
 */
public class MyBatisRefresh implements IHotExtHandler {
    private static Logger logger = Logger.getLogger(MyBatisRefresh.class);


    //清除内核中的_staticConfiguration，写的有点问题
    private boolean hasCleared = false;
    private ArrayList<Configuration> needClearConfigurations;

    @Override
    public byte[] preHandle(ClassLoader classLoader, String path, byte[] content) {
        if(hasCleared) {
            return content;
        }

        try {
            Class<?> defaultSqlSessionFactory = Class.forName("org.apache.ibatis.session.defaults.DefaultSqlSessionFactory", true, classLoader);
            Field staticConfiguration = defaultSqlSessionFactory.getDeclaredField("_staticConfiguration");
            needClearConfigurations = (ArrayList<Configuration>)staticConfiguration.get(null);
            needClearConfigurations.clear();
        } catch (Throwable ignore) {}
        finally {
            hasCleared = true;
        }

        return content;
    }

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> aClass, String s, byte[] bytes) {
        try {
            if(!s.endsWith(".xml")){
                return;
            }

            if(!new String(bytes).contains("<mapper")) {
                return;
            }
            String fileName = s.substring(s.indexOf("/") + 1);


            Set<Configuration> configurations = InstancesHolder.getInstances(Configuration.class);

            for(Configuration configuration : configurations) {
                try{
                    //清理loadedResources
                    clearLoadResource(configuration.getClass(),configuration,"loadedResources",fileName);

                    //重新编译加载资源文件
                    InputStream inputStream = new ByteArrayInputStream(bytes);
                    XMLMapperBuilder configBuilder = new XMLMapperBuilder(inputStream, configuration,
                            s, configuration.getSqlFragments());
                    configBuilder.parse();
                }catch (Exception e) {
                    logger.error("clearLoadResource:"+e.getMessage());
                }
            }

        } catch (Throwable e) {
            logger.error("clearLoadResource err:",e);
        }
    }


    private void removeConfig(Configuration configuration) throws Exception {
        Class classConfig = configuration.getClass();
        clearMap(classConfig, configuration, "mappedStatements");
        clearMap(classConfig, configuration, "caches");
        clearMap(classConfig, configuration, "resultMaps");
        clearMap(classConfig, configuration, "parameterMaps");
        clearMap(classConfig, configuration, "keyGenerators");
        clearMap(classConfig, configuration, "sqlFragments");

        clearSet(classConfig, configuration, "loadedResources");
    }

    @SuppressWarnings("rawtypes")
    private void clearMap(Class classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = null;
        try{
            field = classConfig.getDeclaredField(fieldName);
        }catch (Throwable e) {
            e.printStackTrace();
            field = Configuration.class.getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        Map mapConfig = (Map) field.get(configuration);
        mapConfig.clear();
    }

    @SuppressWarnings("rawtypes")
    private void clearSet(Class classConfig, Configuration configuration, String fieldName) throws Exception {
        Field field = null;
        try{
            field = classConfig.getDeclaredField(fieldName);
        }catch (Throwable e) {
            e.printStackTrace();
            field = Configuration.class.getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        Set setConfig = (Set) field.get(configuration);
        setConfig.clear();
    }

    @SuppressWarnings("rawtypes")
    private void clearLoadResource(Class classConfig, Configuration configuration, String fieldName,String xmlName) throws Exception {
        Field field = null;
        try{
            field = classConfig.getDeclaredField(fieldName);
        }catch (Exception e) {
            field = Configuration.class.getDeclaredField(fieldName);
        }

        String simpleFileName = xmlName.replace("\\","/");
        if(simpleFileName.contains("/")) {
            simpleFileName = xmlName.substring(xmlName.lastIndexOf("/"));
        }

        field.setAccessible(true);
        Set<String> setConfig = (Set<String>) field.get(configuration);
        Iterator<String> iterator = setConfig.iterator();
        while(iterator.hasNext()){
            String res = iterator.next();
            if(res.contains(".xml") && res.contains(simpleFileName)) {
                iterator.remove();
            }
        }
    }

}
