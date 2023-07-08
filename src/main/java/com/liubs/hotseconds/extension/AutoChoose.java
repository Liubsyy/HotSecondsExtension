package com.liubs.hotseconds.extension;

import com.liubs.hotseconds.extension.exception.RemoteItException;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;

import java.util.Iterator;

/**
 * @author Liubsyy
 * 在hot-seconds-remote.xml中配置这个类，智能加载所需要的扩展类
 */
public class AutoChoose implements IHotExtHandler {
    private static final Logger logger = Logger.getLogger(AutoChoose.class);

    @Override
    public byte[] preHandle(ClassLoader classLoader, String path, byte[] content) {
        Iterator<IHotExtHandler> handlerIterator = AllExtensionsManager.getInstance().getAllHandlers().iterator();

        while(handlerIterator.hasNext()){
            IHotExtHandler hotExtHandler = handlerIterator.next();
            try{
                logger.info("preHandle {}",hotExtHandler);
                content = hotExtHandler.preHandle(classLoader,path,content);
            }catch (RemoteItException e) {
                logger.error("Remove handler {}",e,hotExtHandler);
                handlerIterator.remove();
            }catch (Throwable throwable) {
                logger.error("Error in preHandle {}",throwable);
            }
        }
        return content;
    }

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {
        Iterator<IHotExtHandler> handlerIterator = AllExtensionsManager.getInstance().getAllHandlers().iterator();

        while(handlerIterator.hasNext()){
            IHotExtHandler hotExtHandler = handlerIterator.next();
            try{
                logger.info("afterHandle {}",hotExtHandler);
                hotExtHandler.afterHandle(classLoader,classz,path,content);
            }catch (RemoteItException e) {
                logger.error("Remove handler {}",e,hotExtHandler);
                handlerIterator.remove();
            }catch (Throwable throwable) {
                logger.error("Error in afterHandle {}",throwable);
            }
        }

    }
}
