package com.liubs.hotseconds.extension.transform.registry;

import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import com.liubs.hotseconds.extension.cache.VelocityHtmlCacheClear;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;

/**
 * @author Liubsyy
 * @date 2023/7/8 9:17 PM
 * 当检测到org.apache.velocity.runtime.RuntimeSingleton类加载时，那就是项目里使用了velocity
 **/
@ClassTransform
public class VelocityRegistry {
    private static Logger logger = Logger.getLogger(VelocityRegistry.class);

    @OnClassLoad(className = "org.apache.velocity.runtime.RuntimeSingleton")
    public static void registryOnClass() {
        AllExtensionsManager.getInstance().addHotExtHandler(new VelocityHtmlCacheClear());
    }
}
