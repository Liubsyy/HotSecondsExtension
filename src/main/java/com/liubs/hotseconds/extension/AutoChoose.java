package com.liubs.hotseconds.extension;

import com.liubs.hotseconds.extension.exception.RemoteItException;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Liubsyy
 * 在hot-seconds-remote.xml中配置这个类，智能加载所需要的扩展类
 */
public class AutoChoose implements IHotExtHandler {
    private static final Logger logger = Logger.getLogger(AutoChoose.class);

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public byte[] preHandle(ClassLoader classLoader, String path, byte[] content) {
        Iterator<Map.Entry<Class<?>, IHotExtHandler>> handlerIterator =
                AllExtensionsManager.getInstance().getAllHandlers().entrySet().iterator();

        while(handlerIterator.hasNext()){
            IHotExtHandler hotExtHandler =  handlerIterator.next().getValue();
            try{
                logger.info("preHandle {}",hotExtHandler.getClass().getSimpleName());
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
        Iterator<Map.Entry<Class<?>, IHotExtHandler>> handlerIterator =
                AllExtensionsManager.getInstance().getAllHandlers().entrySet().iterator();

        while(handlerIterator.hasNext()){
            Map.Entry<Class<?>, IHotExtHandler> entry = handlerIterator.next();
            Class<?> key = entry.getKey();
            IHotExtHandler hotExtHandler = entry.getValue();
            try{
                logger.info("afterHandle {}",hotExtHandler.getClass().getSimpleName());
                if(hotExtHandler.isSyncRefresh()) {
                    hotExtHandler.afterHandle(classLoader,classz,path,content);
                }else {
                    executorService.execute(() -> {
                        try{
                            hotExtHandler.afterHandle(classLoader,classz,path,content);
                        } catch(RemoteItException e) {
                            logger.error("Remove handler {}",e,hotExtHandler);
                            AllExtensionsManager.getInstance().getAllHandlers().remove(key);
                            handlerIterator.remove();
                        }catch (Throwable throwable) {
                            logger.error("Error in afterHandle {}",throwable);
                        }
                    });
                }

            }catch (RemoteItException e) {
                logger.error("Remove handler {}",e,hotExtHandler);
                handlerIterator.remove();
            }catch (Throwable throwable) {
                logger.error("Error in afterHandle {}",throwable);
            }
        }

    }
}
