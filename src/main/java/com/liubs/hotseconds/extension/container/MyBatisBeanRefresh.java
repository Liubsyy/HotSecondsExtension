package com.liubs.hotseconds.extension.container;

import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.transform.mybatis.MyBatisClassPathMapperScannerPatch;
import com.liubs.hotseconds.extension.transform.mybatis.MyBatisSpringBeanDefinition;
import com.liubs.hotseconds.extension.util.ReflectUtil;
import org.hotswap.agent.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import org.hotswap.agent.util.ReflectionHelper;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;


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
            ClassPathBeanDefinitionScannerAgent scannerAgent = ClassPathBeanDefinitionScannerAgent.getInstance(MyBatisSpringBeanDefinition.getMapperScanner());
            BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(bytes);
            if (beanDefinition != null) {
                scannerAgent.defineBean(beanDefinition);
            }

            //bean name
            BeanNameGenerator beanNameGenerator = (BeanNameGenerator) ReflectionHelper.get(MyBatisSpringBeanDefinition.getMapperScanner(), "beanNameGenerator");
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
