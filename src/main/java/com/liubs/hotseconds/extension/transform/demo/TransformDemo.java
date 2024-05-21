package com.liubs.hotseconds.extension.transform.demo;

import com.liubs.hotseconds.extension.annotations.RemoteMethodTest;
import com.liubs.hotseconds.extension.annotations.ClassTransform;
import com.liubs.hotseconds.extension.annotations.OnClassLoad;
import javassist.*;

/**
 * @author Liubsyy
 * 这个例子主要演示，对一个类加载前进行动态修改，可增加字段方法一类的，使用的工具是javassist
 */
@ClassTransform
public class TransformDemo {

    /**
     * 当 com.liubs.hotseconds.extension.transform.demo.Boy类初始化的时候触发这个函数调用，增加字段int age;
     * @param ctClass
     * @param classPool
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    @OnClassLoad(className = "com.liubs.hotseconds.extension.transform.demo.Boy")
    public static void whenLoadClassBoy(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        try{
            ctClass.addField(CtField.make("public static int age = 22;", ctClass));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RemoteMethodTest("获取插桩后的Boy属性字段，右键->Run method on remote server触发这个函数")
    public static Object printBoy()  {
        return Boy.printAll();
    }

}
