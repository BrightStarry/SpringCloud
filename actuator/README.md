### Spring Cloud 学习
##### actuator(执行器):监控各节点的运行状况
   
在pom中引入
>       <dependency>
>           <groupId>org.springframework.boot</groupId>
>           <artifactId>spring-boot-starter-actuator</artifactId>
>       </dependency>
启动后可以看到多个EndpointHandlerMapping（actuator的原生端点）输出。
例如可以访问 http://localhost/health 查看到status、diskSpace等信息

可以使用 
endpoints.info.path=/aaa 这样的参数，修改默认路径
---
如果许多节点都无法访问，返回401，主要是因为权限的配置不正确
http://blog.csdn.net/u013076044/article/details/60780151
yml中添加management:security:enabled=false
---
!!!如下配置，则需要输入密码，注意，需要引入security依赖
security:
  user:
    password: 970389
    name: zx
  basic:
    path: ["/**"]
---
返回的json需要格式化，可以直接在idea中新建一个a.json，然后复制上去，按c+a+ L

原生端点主要可分为：
1. 应用配置类：获取应用程序中加载的应用配置、环境变量、自动化配置报告等
与SpringBoot应用密切相关的配置类信息。
2. 度量指标类：获取程序运行中用于监控的度量指标，例如内存信息，连接池信息，HTTP请求统计等。
3. 操作控制类:提供了对应用的关闭等操作类功能。

* 应用配置类
Spring框架的注解配置等，使得想要获取到应用中资源和实例的各种关系变得困难，此类端点可以获取到一系列关于Spring应用配置内容的详细报告
基本都是程序启动时确认了的内容，是静态的。
    * /autoconfig 获取应用的自动化配置报告。包括所有自动化配置的候选项。以及每个候选项是否满足自动化配置的先决条件。
        * positiveMatches: 返回条件匹配成功的自动化配置 (不是路径，是访问上面路径json中的key)
        * negativeMatches：返回条件匹配不成功的自动化配置
        例如可以看到许多类没有加载的原因就是缺少前置jar。
    * /beans 获取应用上下文中创建的所有bean.包括bean(bean名)、scope(作用域)、type(bean的java类型)、resource(class路径)、dependencies(依赖的bean名)
    * /configprops 获取应用中配置的属性信息报告(就是配置文件信息)。如果要关闭该端点，则配置：endpoints.configprops.enabled=false
    * /env（环境变量） 获取所有可用的环境属性报告。JVM、环境变量、命令行参数等。
        * 如果属性名包含(password/secret/key等)会*代替。
        * 可以配合@ConfigurationProperties注解，引入程序中使用
    * /mappings 可以显示所有SpringMVC的映射路径。
    * /info 默认为空，如果application中配置了info开头的信息，则返回对应json。
* 度量指标类
动态报告，一些快照信息，如内存使用情况，HTTP请求统计，外部资源指标等。
    * /metrics (度量)各类重要度量指标，如果内存信息，线程信息，垃圾回收信息等.
        * /metrics/{name} 获取更详细的信息，例如metrics/mem.free
    * /health (健康) 获取各类健康指标信息。例如磁盘空间、dataSource连接是否可用、rabbit连接、redis连接、solr连接等。
        * 自带了常用的健康指标检测器，这些通过HealthIndicator接口实现，并会实现引入自动化装配。
        * 如果需要引入没有封装的产品进行开发，例如RocketMQ，需要自己写健康指标检测器，实现HealthIndicator接口。
    * /dump(垃圾场) 暴露程序中运行的线程信息。
        * 使用java.lang.management.ThreadMXBean的dumpAllThreads()方法返回所有含有同步信息的活动线程详情。
    * /trace(痕迹) 返回基本的HTTP跟踪信息。
        * 默认情况下，采用InMemoryTraceRepository类实现的内存方式，保留最近的100条请求记录
* 操作控制类
想要开启该控制类端点，需要配置endpoints.shutdown.enabled=true,其中sensitive这个属性是是否需要密码(还需要配置security才行),path可以改变shutdown路径。
    * /shutdown（这个需要POST请求） 原生端点中，只有这一个操作端点，且需要加密使用。使用Eureka后可增加更多.
    



   