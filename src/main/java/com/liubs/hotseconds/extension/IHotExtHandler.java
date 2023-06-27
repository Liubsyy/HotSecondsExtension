package com.liubs.hotseconds.extension;

/**
 * @author Liubsyy
 * @Date: 2023/4/28 15:41
 * Description: Implements this interface and add classname in hot-seconds-remote.xml
 */
public interface IHotExtHandler {

    /**
     * before you hotswap file
     * @param classLoader
     * @param path
     * @param content
     */
    void preHandle(ClassLoader classLoader, String path, byte[] content);

    /**
     * after you hotswap file
     * @param classLoader
     * @param classz : if file is not java class ,classz is null
     * @param path : upload to path
     * @param content
     */
    void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content);
}
