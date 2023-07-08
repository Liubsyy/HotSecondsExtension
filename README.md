# HotSecondsServer 插件扩展通用包

这个工程主要是用于扩展[HotSeconds](https://github.com/thanple/HotSecondsIDEA)热部署逻辑，兼容更多的第三方开源框架，大家可直接Fork项目提交代码，后续再合并一下，众人拾柴火焰高，本插件的理念是：一切皆可热部署。

<br>

## 准备条件
请确保HotSecondsServer版本>=****HotSecondsServer-beat****，然后再使用此扩展包

<br>

## 插件内核已支持
| 开源组件                                         | 范围                              |
|----------------------------------------------|---------------------------------|
| Spring                                       | 包括Spring，SpringMVC，SpringBoot生态 |
| MyBatis                                      | 支持新增/修改 接口和.xml文件            |
| MyBatis Plus                                 | 同MyBatis                        |
| Hibernate                                    | 支持Hibernate2和Hibernate3         |
| Freemarker<br/>Thymeleaf<br/>Velocity-Spring | 刷新缓存                            |

<br>

## 本扩展包已支持
| 组件     | Class                                 | 范围           |
|----------|---------------------------------------|--------------|
| Velocity | com.liubs.hotseconds.extension.cache.VelocityHtmlCacheClear | 刷新html缓存     |
| Mybatis  | com.liubs.hotseconds.extension.container.MyBatisRefresh | 一个demo，内核已支持 |

<br>

## 扩展包使用

>1.pom引用本工程的包
>
>2.在hot-seconds-remote.xml中配置com.liubs.hotseconds.extension.AutoChoose
>
> 然后启动项目即可

<br>

## 扩展包开发步骤

>1.写一个类，实现 IHotExtHandler接口
>
>2.在适当的时机注册这个类到 AllExtensionsManager
> 
> &nbsp; &nbsp; &nbsp; (1)可以在某个特殊类初始化的时候注册，demo: VelocityRegister
> 
> &nbsp; &nbsp; &nbsp; (2)也可以直接在AllExtensionsManager写死


<br>

## 扩展包开发例子

[刷新velocity缓存](https://github.com/Liubsyy/HotSecondsExtension/blob/master/doc/%E5%86%99%E6%89%A9%E5%B1%95%E5%8C%85%E4%BE%8B%E5%AD%90.md)

[动态增强类(加载类时)](https://github.com/Liubsyy/HotSecondsExtension/blob/master/src/main/java/com/liubs/hotseconds/extension/transform/demo/TransformDemo.java)


