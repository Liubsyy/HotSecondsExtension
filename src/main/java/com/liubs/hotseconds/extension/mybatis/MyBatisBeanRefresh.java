package com.liubs.hotseconds.extension.mybatis;

import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.transform.mybatis.MyBatisClassPathMapperScannerPatch;
import com.liubs.hotseconds.extension.transform.mybatis.MyBatisSpringBeanDefinition;
import com.liubs.hotseconds.extension.util.ReflectUtil;
import org.apache.ibatis.session.Configuration;
import org.hotswap.agent.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * 新增mapper接口热部署
 * 当新增一个mapper接口的时候，给mapper接口生成代理对象并注册到spring中
 * @author Liubsyy
 * @date 2023/7/9 9:22 PM
 */
public class MyBatisBeanRefresh implements IHotExtHandler {
    private static Logger logger = Logger.getLogger(MyBatisClassPathMapperScannerPatch.class);

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] bytes) {
        if(classz == null || !classz.isInterface()) {
            return;
        }
        if(classz.getAnnotation(org.apache.ibatis.annotations.Mapper.class) == null) {
            return;
        }
        if(null == MyBatisSpringBeanDefinition.getMapperScanner()) {
            return;
        }
        try {
            Class<?> sqlSessionFactoryClz = Class.forName("org.apache.ibatis.session.defaults.DefaultSqlSessionFactory",true,classLoader);
            Field staticConfiguration = null;
            try{
                staticConfiguration = sqlSessionFactoryClz.getDeclaredField("_staticConfiguration");
            }catch (NoSuchFieldException ex) {
                return;
            }
            Configuration configuration = ((ArrayList<Configuration>)staticConfiguration.get(null)).get(0);


            //这里用类字符串判断是否mybatis plus，不引用mybatis plus的类，避免应用程序没有用mybatis plus而报错
            if(configuration.getClass().getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                MyBatisPlusMapperUpdate.refreshMapper(configuration,classz);
                //return;
            }


            ClassPathBeanDefinitionScannerAgent scannerAgent = ClassPathBeanDefinitionScannerAgent.getInstance(MyBatisSpringBeanDefinition.getMapperScanner());
            BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(bytes);
            if (beanDefinition != null) {
                scannerAgent.defineBean(beanDefinition);
            }

            //bean name
            BeanNameGenerator beanNameGenerator = (BeanNameGenerator) ReflectUtil.get(MyBatisSpringBeanDefinition.getMapperScanner(), "beanNameGenerator");
            BeanDefinitionRegistry registry = ReflectUtil.getField(scannerAgent,"registry");
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);

            //beanDefinitionHolder
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);

            MyBatisSpringBeanDefinition.mybatisBeanDefinition(definitionHolder);

        } catch (Exception e) {
            logger.error("Refresh Mybatis Bean err",e);
        }
    }


}
