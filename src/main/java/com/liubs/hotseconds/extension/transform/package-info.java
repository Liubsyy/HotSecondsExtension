package com.liubs.hotseconds.extension.transform;


/**
 * 这个包目录会被扫描，在某个类初始化的时候可以对该类进行增强和插桩，demo: TransformDemo
 * 1.新建一个类，加上ClassTransform注解
 * 2.函数上面加上OnClassLoad注解
 * OnClassLoad.className 这个类在初始化的时候，会回调加上OnClassLoad注解的函数
 *
 * 请注意：如果一个类A,有个方法注解标记 OnClassLoad(className=B)，那么在A中不要import任何B相关的类
 * 原因：如果A 中import了B，那么B先加载完了，而OnClassLoad标记的B还没初始化
 */