package com.liubs.hotseconds.extension.transform.demo;

import com.liubs.hotseconds.extension.annotations.RemoteMethodTest;
import com.liubs.hotseconds.extension.transform.ClassTransform;
import com.liubs.hotseconds.extension.transform.OnClassLoad;
import javassist.*;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;

@ClassTransform
public class MyBatisAddConfigurationDemo {

    /**
     * 当 DefaultSqlSessionFactory加载的时候插入字段保存Configuration
     * @param ctClass
     * @param classPool
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    @OnClassLoad(className = "org.apache.ibatis.session.defaults.DefaultSqlSessionFactory")
    public static void patchDefaultSqlSessionFactory(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        ctClass.addField(CtField.make("public static java.util.ArrayList  _myConfigurationDemo = new java.util.ArrayList();", ctClass));
        CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[] { classPool.get("org.apache.ibatis.session.Configuration")});
        constructor.insertAfter("{_staticConfiguration.add($1);}");
    }



    @RemoteMethodTest("获取Mybatis中插桩的Configuration，上面的patchDefaultSqlSessionFactory插桩完会有这个字段")
    public static Configuration getMyBatisConfiguration() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> defaultSqlSessionFactory = Class.forName("org.apache.ibatis.session.defaults.DefaultSqlSessionFactory");
        Field staticConfiguration = defaultSqlSessionFactory.getDeclaredField("_myConfigurationDemo");
        ArrayList configurations = (ArrayList)staticConfiguration.get(null);
        return (Configuration)configurations.get(0);
    }

}
