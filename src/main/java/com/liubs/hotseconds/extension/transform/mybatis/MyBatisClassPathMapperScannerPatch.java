package com.liubs.hotseconds.extension.transform.mybatis;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.holder.InstancesHolder;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;
import com.liubs.hotseconds.extension.mybatis.MyBatisRefresh;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;


/**
 * 插桩获取ClassPathMapperScanner实例
 *
 * @author Liubsyy
 * @date 2023/7/9 8:22 AM
 **/

@ClassTransform
public class MyBatisClassPathMapperScannerPatch {
    private static Logger logger = Logger.getLogger(MyBatisClassPathMapperScannerPatch.class);

    /**
     *  ClassPathMapperScanner 构造函数插桩，获取ClassPathMapperScanner实例
     */
    @OnClassLoad(className = "org.mybatis.spring.mapper.ClassPathMapperScanner")
    public static void patchMyBatisClassPathMapperScanner(CtClass ctClass, ClassPool classPool){
        logger.info("MyBatisBeanRefresh.patchMyBatisClassPathMapperScanner");
        try{
            CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[] {
                    classPool.get("org.springframework.beans.factory.support.BeanDefinitionRegistry") });
            constructor.insertAfter("{com.liubs.hotseconds.extension.transform.mybatis.MyBatisSpringBeanDefinition.loadScanner(this);}");
        }catch (Throwable e) {
            logger.error("patchMyBatisClassPathMapperScanner err",e);
        }
    }



    @OnClassLoad(className = "org.apache.ibatis.session.Configuration")
    public static void registerConfiguration(CtClass ctClass){
        InstancesHolder.insertObjectCacheInConstructorWithBaseClassKey(ctClass);
        AllExtensionsManager.getInstance().addHotExtHandler(new MyBatisRefresh());
    }
}
