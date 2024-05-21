package com.liubs.hotseconds.extension.patch;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.logging.Logger;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * 针对Spring5一些不兼容场景的补丁
 * @author Liubsyy
 * @date 2024/3/9
 **/
@ClassTransform
public class Spring5Patch {
    private static Logger logger = Logger.getLogger(Spring5Patch.class);


    /**
     * SpringPlugin设置了cacheBeanMetadata为false，导致 RootBeanDefinition.predictBeanType不会执行
     * 最终会导致 spring5中 DefaultListableBeanFactory.findAnnotationOnBean为null
     * @param ctClass
     * @param classPool
     */
    @OnClassLoad(className = "org.springframework.beans.factory.support.AbstractBeanFactory")
    public static void patchPredictBeanType(CtClass ctClass, ClassPool classPool) {
        try {
            CtMethod method = ctClass.getDeclaredMethod("getMergedLocalBeanDefinition",
                    new CtClass[] {classPool.get("java.lang.String")});

            method.insertAfter("{"
                    + "if ($_!=null) {"
                    + "    isFactoryBean($1, $_);"
                    + "}"
                    + "}", true); // true 表示处理的是一个 finally 块，确保即使有返回语句，插入的代码也会执行

        } catch (Exception e) {
            logger.error("patchPredictBeanType err",e);
        }
    }
}
