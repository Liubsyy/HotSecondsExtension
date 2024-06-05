package com.liubs.hotseconds.extension.jacoco;

import com.liubs.findinstances.jvmti.InstancesOfClass;
import com.liubs.hotseconds.extension.IHotExtHandler;
import com.liubs.hotseconds.extension.exception.RemoteItException;
import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.util.FileUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * jacoco覆盖率支持
 * @author Liubsyy
 * @date 2024/6/5
 **/
public class JacocoTransform implements IHotExtHandler {
    private static final Logger logger = Logger.getLogger(JacocoTransform.class);


    private static boolean INIT_INTERNAL_PACKAGE_NAME = false;
    private String internalPackageName = null;

    private Object classFileDumper;
    private Method dumpMethod;


    private Object instrumenter;
    private Method instrumentMethod;

    @Override
    public byte[] preHandle(ClassLoader classLoader, String path, byte[] originBytes) {

        if(!FileUtil.isClassFile(originBytes)) {
            return originBytes;
        }

        if(!INIT_INTERNAL_PACKAGE_NAME) {
            INIT_INTERNAL_PACKAGE_NAME = true;
            internalPackageName = getInternalPackageName();
        }
        //没有org.jacoco.agent.rt.internal包表示没有使用jacoco
        if(null == internalPackageName) {
            return originBytes;
        }

        try{
            if(null == classFileDumper) {
                Class<?> classFileDumperClass = Class.forName(internalPackageName + ".ClassFileDumper");
                classFileDumper = InstancesOfClass.getInstanceList(classFileDumperClass,1).get(0);
                dumpMethod = classFileDumperClass.getDeclaredMethod("dump", String.class, byte[].class);
            }


            if(null == instrumenter) {
                Class<?> instrumenterClass = Class.forName(internalPackageName+".Instrumenter");
                instrumenter = InstancesOfClass.getInstanceList(instrumenterClass,1).get(0);
                instrumentMethod = instrumenterClass.getDeclaredMethod("instrument",byte[].class,String.class);
            }

        }catch (Exception e) {
            throw new RemoteItException(e);
        }

        try{
            dumpMethod.invoke(classFileDumper, path, originBytes);
            return (byte[])instrumentMethod.invoke(instrumenter, originBytes,path);
        }catch (Exception e) {
            logger.error("jacoco transform err",e);
        }

        return originBytes;
    }



    //包名是形如: org.jacoco.agent.rt.internal_c13123e.CoverageTransformer
    private static String getInternalPackageName(){
        try{
            // 获取 JaCoCo Agent 类加载器
            Class<?> agentClass = Class.forName("org.jacoco.agent.rt.RT");
            Method getAgentMethod = agentClass.getDeclaredMethod("getAgent");
            Object agent = getAgentMethod.invoke(null);

            // 获取 JaCoCo 内部类包的名称
            Field runtimeDataField = agent.getClass().getDeclaredField("runtimeData");
            runtimeDataField.setAccessible(true);
            Object runtimeData = runtimeDataField.get(agent);

            // 反射访问 internal 包名
            String internalPackageName = runtimeData.getClass().getPackage().getName();

            return internalPackageName;

        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void afterHandle(ClassLoader classLoader, Class<?> classz, String path, byte[] content) {}
}
