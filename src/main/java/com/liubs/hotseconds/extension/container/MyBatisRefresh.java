package com.liubs.hotseconds.extension.container;

import com.liubs.hotseconds.extension.IHotExtHandler;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * mybatis配置.xml修改完热部署
 * 这个例子仅供参考
 */
public class MyBatisRefresh implements IHotExtHandler {

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> aClass, String s, byte[] bytes) {
        try {
            if(!s.endsWith(".xml")){
                return;
            }
            if(!new String(bytes).contains("mapper")) {
                return;
            }

            //TODO 这里修改一下spring context,获取Spring容器
            ApplicationContext springContext = null;
            SqlSessionFactory factory = springContext.getBean(SqlSessionFactory.class);
            Configuration configuration = factory.getConfiguration();

            //缓存统统清掉
            this.removeConfig(configuration);

            //重新编译加载资源文件
            InputStream inputStream = new ByteArrayInputStream(bytes);
            XMLMapperBuilder configBuilder = new XMLMapperBuilder(inputStream, configuration,
                    s, configuration.getSqlFragments());


            configBuilder.parse();
        } catch (Exception e) {
            e.printStackTrace();
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
        }catch (Exception e) {
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
        }catch (Exception e) {
            e.printStackTrace();
            field = Configuration.class.getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        Set setConfig = (Set) field.get(configuration);
        setConfig.clear();
    }

}
