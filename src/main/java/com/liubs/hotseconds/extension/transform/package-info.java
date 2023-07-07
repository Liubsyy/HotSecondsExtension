package com.liubs.hotseconds.extension.transform;


/**
 * 这个包目录会被扫描，在某个类初始化的时候可以对该类进行增强和插桩，demo: TransformDemo
 * 1.新建一个类，加上ClassTransform注解
 * 2.函数上面加上OnClassLoad注解
 * OnClassLoad.className 这个类在初始化的时候，会回调加上OnClassLoad注解的函数
 *
 */