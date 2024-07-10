package com.liubs.hotseconds.extension.holder;

import com.liubs.hotseconds.extension.logging.Logger;
import com.liubs.hotseconds.extension.manager.AllExtensionsManager;
import javassist.CtClass;
import javassist.CtConstructor;

import java.util.*;

/**
 * 实例缓存器
 * 使用步骤：
 * 1. 在 @OnClassLoad函数里面加上 InstancesHolder.insertObjectCacheInConstructor(ctClass);
 * 2. 在需要的时候InstancesHolder.getInstances(class)
 * @author Liubsyy
 * @date 2024/5/8
 */
public class InstancesHolder {
    private static final Logger logger = Logger.getLogger(AllExtensionsManager.class);

    private static final Map<Class<?>, Set<Object>> instancesHolder = new HashMap<>();
    private static final Set<String> alreadyHold = Collections.synchronizedSet(new HashSet<>());


    /**
     * 根据Class获取对象实例引用
     * @param klass
     * @return
     * @param <T>
     */
    public static <T> Set<T> getInstances(Class<T> klass) {
        Set<T> results = new HashSet<>();
        Set<Object> objects = instancesHolder.get(klass);
        if(null != objects) {
            results.addAll((Set<T>)objects);
        }
        return results;
    }


    /**
     * 插入的是当前类为key
     * @param ctClass
     */
    public static void insertObjectCacheInConstructor(CtClass ctClass) {
        try{
            String className = ctClass.getName();

            //只插桩一次
            if(alreadyHold.contains(className)) {
                return;
            }
            for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
                String src = "{com.liubs.hotseconds.extension.holder.InstancesHolder.insertHolder(this);}";
                constructor.insertAfter(src);
            }

            alreadyHold.add(className);
        }catch (Exception e) {
            logger.error("insertObjectCacheInConstructor err,class={}",e,ctClass.getName());
        }

    }

    /**
     * 插入的是以ctClass为基类的key
     * @param ctClass
     */
    public static void insertObjectCacheInConstructorWithBaseClassKey(CtClass ctClass) {
        try{
            String className = ctClass.getName();

            //只插桩一次
            if(alreadyHold.contains(className)) {
                return;
            }
            for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
                String src = "{com.liubs.hotseconds.extension.holder.InstancesHolder.insertHolder(\""+ctClass.getName()+"\",this);}";
                constructor.insertAfter(src);
            }

            alreadyHold.add(className);
        }catch (Exception e) {
            logger.error("insertObjectCacheInConstructor err,class={}",e,ctClass.getName());
        }

    }

    public static void insertHolder(Object obj){
        synchronized (instancesHolder) {
            //使用Weak引用
            Set<Object> objects = instancesHolder.computeIfAbsent(obj.getClass(), k -> Collections.newSetFromMap(new WeakHashMap<>()));
            objects.add(obj);
        }
    }
    public static void insertHolder(String className,Object obj){
        synchronized (instancesHolder) {
            //使用Weak引用
            Set<Object> objects = null;
            try {
                objects = instancesHolder.computeIfAbsent(Class.forName(className),
                        k -> Collections.newSetFromMap(new WeakHashMap<>()));
                objects.add(obj);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
