package com.liubs.hotseconds.extension.patch;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.logging.Logger;
import javassist.*;
import org.hotswap.agent.plugin.spring.getbean.EnhancerProxyCreater;

/**
 * 一些零碎补丁入口
 * @author Liubsyy
 * @date 2024/7/9
 **/
@ClassTransform
public class PatchEntrance {
    private static Logger logger = Logger.getLogger(Spring5Patch.class);

    /**
     * DynamicDataSourceCreatorAutoConfiguration导致启动失败问题
     * 只涉及到dynamic-datasource-spring-boot-starter的3.3-3.4版本，3.5已没有这个字段
     * 动态数据源，DynamicDataSourceCreatorAutoConfiguration.properties去掉final修饰
     * @param ctClass
     */
    @OnClassLoad(className = "com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration")
    public static void patchDynamicDataSource_3_3_4(CtClass ctClass) {
        try {
            CtField ctField = null;
            try {
                ctField = ctClass.getDeclaredField("properties");
            } catch (NotFoundException ignored) {}
            if(null == ctField) {
                return;
            }
            ctField.setModifiers(ctField.getModifiers() & ~Modifier.FINAL);
            Class.forName(EnhancerProxyCreater.class.getName());    //触发EnhancerProxyCreater的补丁
        } catch (Exception e) {
            logger.error("patchDynamicDataSource err", e);
        }
    }


    /**
     * EnhancerProxyCreater的补丁，生成动态代理后设置属性
     * @param ctClass
     * @param classPool
     */
    @OnClassLoad(className = "org.hotswap.agent.plugin.spring.getbean.EnhancerProxyCreater")
    public static void patchEnhancerProxyCreater(CtClass ctClass, ClassPool classPool) {


        try {
            try {
                Class.forName("com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceCreatorAutoConfiguration");
            } catch (ClassNotFoundException e) {
                //不存在DynamicDataSourceCreatorAutoConfiguration，不打补丁
                return;
            }
            CtMethod method = ctClass.getDeclaredMethod("doCreate",
                    new CtClass[]{
                            classPool.get("java.lang.Object"),
                            classPool.get("java.lang.Object"),
                            classPool.get("java.lang.Class[]"),
                            classPool.get("java.lang.Object[]")});
            method.insertAfter("{ if($2 != $_) {com.liubs.hotseconds.extension.patch.DynamicDataSourcePatch.copyProperties($2,$_);}}", true);
        } catch (Exception e) {
            logger.error("patchEnhancerProxyCreater err",e);
        }

    }

}
