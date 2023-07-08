package com.liubs.hotseconds.extension.transform.register;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.cache.VelocityHtmlCacheClear;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;
import javassist.*;

/**
 * @author Liubsyy
 * @date 2023/7/8 9:17 PM
 * 当检测到org.apache.velocity.runtime.RuntimeSingleton类加载时，那就是项目里使用了velocity
 **/
@ClassTransform
public class VelocityRegister {

    @OnClassLoad(className = "org.apache.velocity.runtime.RuntimeSingleton")
    public static void register(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        AllExtensionsManager.getInstance().addHotExtHandler(new VelocityHtmlCacheClear());
    }
}
