package com.liubs.hotseconds.extension.transform.spring;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.logging.Logger;
import org.hotswap.agent.javassist.ClassPool;
import org.hotswap.agent.javassist.CtClass;
import org.hotswap.agent.javassist.CtConstructor;
import org.hotswap.agent.util.ReflectionHelper;
import org.springframework.core.io.ResourceLoader;

/**
 * 兼容Spring5以SpringBoot FatJar启动时spring失效的问题
 * 修复ResourceLoader中classLoader获取为空的问题
 * @author Liubsyy
 * @date 2023/9/25 1:14 AM
 **/
@ClassTransform
public class Spring5ResourceLoaderInjectClassLoaderPatch {
    private static Logger logger = Logger.getLogger(Spring5ResourceLoaderInjectClassLoaderPatch.class);

    @OnClassLoad(className = "org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider")
    public static void patchClassLoader(CtClass ctClass, ClassPool classPool){
        logger.info("pre patchClassLoader");
        try{
            CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[] {
                    classPool.get("java.lang.Boolean"),
                    classPool.get("org.springframework.core.env.Environment") });
            constructor.insertAfter("{com.liubs.hotseconds.extension.transform.spring.Spring5ResourceLoaderInjectClassLoaderPatch.patchClassLoader(this);}");
        }catch (Throwable e) {
            logger.error("pre patchClassLoader err",e);
        }
    }

    public static void patchClassLoader(org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider) {
        try{
            org.springframework.core.type.classreading.MetadataReaderFactory metadataReaderFactory = classPathScanningCandidateComponentProvider.getMetadataReaderFactory();
            org.springframework.core.io.ResourceLoader resourceLoader = (ResourceLoader) ReflectionHelper.get(metadataReaderFactory, "resourceLoader");
            Object classLoader = ReflectionHelper.get(resourceLoader, "classLoader");
            if(null == classLoader
                    && resourceLoader.getClass().getClassLoader().getClass().getSimpleName().contains("AppClassLoader")) {
                ReflectionHelper.set(resourceLoader,resourceLoader.getClass(),"classLoader",resourceLoader.getClass().getClassLoader());
            }
        }catch (Throwable e) {
            logger.error("patchClassLoader err",e);
        }
    }
}
