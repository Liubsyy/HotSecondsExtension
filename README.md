

## HotSecondsServer 插件扩展通用包

这个工程主要是用于扩展[HotSecondsServer](https://github.com/Liubsyy/HotSecondsIDEA)热部署逻辑，兼容更多的第三方开源框架，热部署第三方框架说简单也简单，说复杂也复杂，简单的点是，只要懂框架原理就能按照加载的原理重新增量加载/注册即可（先清理缓存），复杂的点是，需要考虑每个版本每个框架在不同的场景下的兼容性，需要花费一定的时间和精力。大家可直接Fork项目提交代码，后续再合并一下，众人拾柴火焰高，本插件的理念是：一切皆可热部署。
<br><br>

### 扩展包更新范围
| 版本             | 更新范围                                         |mvn发布时间|
|-----------------|--------------------------------------------------|-----------------|
| 1.0.4           | spring5 findAnnotationOnBean问题，mybatis实体新增字段，modelmapper清除缓存 |待发布|
| 1.0.1           | SpringMVC json缓存清除 |2024-1-1|
| 1.0.0           | MyBatis和MyBatis Plus支持新增mapper接口和xml配置文件 |2023-09-23|

<br>

### 扩展包使用


1. 首先在maven 的pom.xml引用本工程的包
   ```
   <dependency>
      <groupId>io.github.liubsyy</groupId>
      <artifactId>HotSecondsExtension</artifactId>
      <version>1.0.3</version>
   </dependency>
   ```

2. 然后在hot-seconds-remote.xml中配置 com.liubs.hotseconds.extension.AutoChoose，会自动智能选择需要的扩展组件，然后启动项目即可
   ```
    <dev-ext>
        <classname>com.liubs.hotseconds.extension.AutoChoose</classname>
    </dev-ext>
   ```



### 扩展包开发流程

#### 开发步骤

>1.写一个类，实现 IHotExtHandler接口
>
>2.在适当的时机注册这个类到 AllExtensionsManager
> 
> &nbsp; &nbsp; &nbsp; (1)可以在某个特殊类初始化的时候注册，demo: VelocityRegistry
> 
> &nbsp; &nbsp; &nbsp; (2)也可以直接在AllExtensionsManager写死

#### 扩展包开发例子

[刷新velocity缓存](https://github.com/Liubsyy/HotSecondsExtension/blob/master/doc/%E5%86%99%E6%89%A9%E5%B1%95%E5%8C%85%E4%BE%8B%E5%AD%90.md)

[动态增强类(加载类时)](https://github.com/Liubsyy/HotSecondsExtension/blob/master/src/main/java/com/liubs/hotseconds/extension/transform/demo/TransformDemo.java)

#### 获取某个类的对象实例
无需代码维护具体的对象，也无需再构造函数插入字节码获取对象，可使用[FindInstancesOfClass](https://github.com/Liubsyy/FindInstancesOfClass)这个工具，直接根据类获取对象实例


