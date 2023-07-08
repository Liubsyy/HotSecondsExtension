package com.liubs.hotseconds.extension.transform.velocity;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.cache.VelocityHtmlCacheClear;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;
import javassist.*;

/**
 * @author Liubsyy
 * @date 2023/7/8 9:17 PM
 **/
@ClassTransform
public class VelocityRegister {

    @OnClassLoad(className = "org.apache.velocity.runtime.RuntimeSingleton")
    public static void register(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        AllExtensionsManager.getInstance().addHotExtHandler(new VelocityHtmlCacheClear());
    }
}
