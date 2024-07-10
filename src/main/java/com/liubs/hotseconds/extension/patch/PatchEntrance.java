package com.liubs.hotseconds.extension.patch;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.logging.Logger;
import javassist.*;

/**
 * 一些零碎补丁入口
 * @author Liubsyy
 * @date 2024/7/9
 **/
@ClassTransform
public class PatchEntrance {
    private static Logger logger = Logger.getLogger(Spring5Patch.class);

    /**
     * EnhancerProxyCreater的补丁，生成动态代理后设置属性
     * @param ctClass
     * @param classPool
     */
    @OnClassLoad(className = "org.hotswap.agent.plugin.spring.getbean.EnhancerProxyCreater")
    public static void patchEnhancerProxyCreater(CtClass ctClass, ClassPool classPool) {

        try {

            /**
             * DynamicDataSourceCreatorAutoConfiguration导致启动失败问题
             * 只涉及到dynamic-datasource-spring-boot-starter的3.3-3.4版本，3.5已没有这个字段
             */
            try {
                //存在DynamicDataSourceCreatorAutoConfiguration.properties字段 , 则打下面的补丁
                CtClass dynamicDataSourceClass = classPool.get("com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration");
                dynamicDataSourceClass.getDeclaredField("properties");

                //DynamicDataSourceCreatorAutoConfiguration打上补丁
                CtMethod method = ctClass.getDeclaredMethod("doCreate",
                        new CtClass[]{
                                classPool.get("java.lang.Object"),
                                classPool.get("java.lang.Object"),
                                classPool.get("java.lang.Class[]"),
                                classPool.get("java.lang.Object[]")});
                method.insertAfter("{ if($2 != $_) {com.liubs.hotseconds.extension.patch.DynamicDataSourcePatch.copyProperties($2,$_);}}", true);
                logger.info("patchEnhancerProxyCreater,insert copyProperties");
            } catch (NotFoundException ignore) {}


        } catch (Exception e) {
            logger.error("patchEnhancerProxyCreater err",e);
        }

    }

}
