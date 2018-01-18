# Spring Cloud
## Eureka 
#### 阶段1 ： 开启服务中心集群(3个节点),注册一个服务（该服务有两个实例，就是两台机器提供该服务），一个消费者对该服务进行负载均衡的消费
eureka-server：服务中心项目，通过jar的方式，修改运行参数，启动3个节点。
eureka-client：服务提供者，向服务中心注册自己，提供服务；使用jar的方式，修改运行参数，启动2个实例.
ribbon-consumer:服务消费者，连接服务中心，获取eureka-client提供的服务，进行调用；使用ribbon进行负载均衡，保证对每个服务实例
进行轮询，也就是访问10次，结果是，对每个eureka-client服务实例访问了5次。
---
##### 服务续约(Renew)：
向服务注册中心(server)注册完服务后，服务提供者(client)会维持一个心跳连接用来告诉server自己还活着。防止server的剔除任务将它从服务列表剔除出去。
>        eureka:
>          instance:
>            lease-expiration-duration-in-seconds: 90
>            lease-renewal-interval-in-seconds: 30

lease-expiration-duration-in-seconds :续约期满持续时间，也就是服务失效时间，默认90s
lease-renewal-interval-in-seconds ： 续约恢复间隔，续约服务调用的间隔，也就是心跳连接的间隔。默认30s
---
该心跳检测只能保证服务实例运行正常，并不保证提供的服务一定可用(例如数据库炸了).
所以可以使用actuator中的/health路径，来检测实例是否健康。
如下配置：
1. 引入actuator依赖。
2. eureka.client.healthcheck.enabled=true 开启该检测
3. 如果服务实例(客户端)修改过/health的路径等，还需要另外配置，《SpringCloud微服务实战》P70

---
##### 服务消费者获取服务
服务消费者消费时，会发送一个REST请求给服务注册中心，来获取服务清单。
Eureka会维护一份只读的服务清单来返回给客户端，同时该缓存清单每隔30s刷新一次。

eureka.client.fetch-registry该属性表示 是否拥有获取清单的功能，默认为true
eureka.client.registry-fetch-interval-seconds该属性表示只读清单更新时间，默认30s
---
##### 服务调用
服务消费者获取清单后，通过服务名获取提供该服务的实例名和该实例的元数据信息。由此根据需要决定调用该服务的哪个实例。
Ribbon中，默认使用轮询的方式进行调用，从而实现负载均衡。

元数据(服务名称、实例名称、实例ip、实例端口等)。
eureka.instance.metadataMap.<properties>=<value> 
可通过如上参数对元数据进行配置

对于服务实例的选择，Eureka中有Region（地区）和Zone(地区)的概念。
一个Region包含多个Zone，每个服务客户端被注册到一个Zone中。所以每个服务客户端对应一个Region和一个Zone。
在进行服务调用时，优先使用同一个Zone中的服务提供方。若访问不到，访问其他Zone。

一个微服务应用只可以属于一个Region，如果不配置，默认为default，
eureka.client.region 这个参数可以配置region空间。
如果没有配置Zone，也会有一个默认的zone，这也是之前参数defaultZone的由来。
eureka.client.availability-zones 可以设置zone，并且可以设置多个，使用 ","分割。

Ribbon来实现服务调用时，会默认优先调用与客户端处于同一个Zone的服务端实例，如果该Zone中没有，才会去其他Zone中找。
由此，可以设计出对于区域性故障的容错集群。
---
#### 服务下线
服务客户端中，当服务实例正常关闭时，会触发一个下线的REST请求给Eureka Server。
服务中心收到请求后，将服务状态设置为下线(DOWN).

如果是非正常关闭，例如内存溢出，网络故障等，使服务无法正常工作，服务中心将无法收到下线请求。
Eureka Server因此有一个定时任务，每隔一段时间(默认60s)，会将清单中超时(默认90s，就是上面的lease-expiration-duration-in-seconds属性)
的服务剔除。
---
#### 自我保护
服务注册中心会统计之前的心跳连接的失败比例，如果15分钟内成功率低于85%,
它会将当前的服务实例注册信息保护起来，让实例不会过期，尽可能保护这些注册信息。
但是如果保护期间实例出现问题，客户端很容易拿到实际不存的服务实例，出现调用失败，
所以客户端需要有容错机制，例如请求重试，断路器等机制。

由于本地测试很容易触发该保护机制，所以，本地开发，可用下列参数关闭保护机制，让服务注册中心将不可以用的实例正确剔除。
eureka.server.enable-self-preservation=false
---
---
### RestTemplate详解 --消费者使用该类，配合Ribbon负载均衡地调用服务实例(ribbon-consumer中有例子)
* GET请求
    1. getForEntity()，该方法返回ResponseEntity对象。这个对象是对HTTP请求响应的封装。主要包含，请求状态(200等)、他的父类HttpEntity
    中还有请求头信息HttpHeaders以及泛型类型的请求体对象。
    
        * 如下，最后一个参数会替换掉{1},而返回的ResponseEntity对象中的body内容会转为第二个参数的类类型String
        (如果希望返回User类型的body，可以把String.class改为User.class)
        {1}这个占位符，第三个参数其实是Object...arg 这样的类型，进行绑定。
        {name}使用这样的占位符时，第三个参数可以传入Map，使用key进行绑定。
        此外，也可以不需要第三个参数，不过需要把第一个String类型的参数换为java.net.URI这个类对象。
        ---
           ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://USER-SERVER/user?name={1}",String.class,"zx");
           String body = responseEntity.getBody();
        ---
    2. getForObject()，该方法是对getForEntity()进一步的封装，写法和上面的基本一样。都是
        使用Object...args 或者 Map 或者 URI。只不过返回参数从ResponseBody变成了第二个参数传入的类型。
        也就是说，相当于直接返回了Body。
* POST请求
    1. postForEntity()，类似getForEntity()方法。不过参数大致成了(String,Object,Class<T>).第一个还是url，第二个是post请求的参数，第三个是返回的body的类类型。
    同样也可以使用map、uri等。
    2. postForObject()，类似getForObject()方法。
    3. postForLocation()，同上面的一样，都是Object...或Map或URI，不过这个方法的返回类型只会是URI类型，返回的就是新资源的URI
* PUT请求
    1. put()方法，该方法同样是Object...或Map或URI，不过该方法是void。
* DELETE请求
    1. delete()方法，大致同GET请求一样，也是将参数绑定在URL中，不过稍有不同，例如http://user-server/user/1，就是只删除id为1的用户。其他同上。
---
---
#### Ribbon 参数配置
* 全局配置：使用 ribbon.<key>=<value>这样的格式配置，
例如ribbon.ConnectionTimeout=250 配置Ribbon创建连接的超时时间。
* 客户端配置：使用 <client>.ribbon.<key>=<value> 客户端配置会覆盖全局配置。
---
SpringCloud整合了SpringRetry来增强RestTemplate的容错能力。例如如下配置:
spring.cloud.loadbalancer.retry.enabled 是否开启重试机制，默认为false。
<client>.ribbon.Connection 请求连接超时时间
<client>.ribbon.ReadTimeout 请求处理超时时间
<client>.ribbon.OKToRetryOnAllOperations 对所有操作请求都进行重试
<client>.ribbon.MaxAutoRetriesNextServer 自动切换实例进行重试的最大次数
<client>.ribbon.MaxAutoRetries 对同一个实例（当前实例）的重试次数
---
---
####问题记录
如果访问服务中心，在Instances currently registered with Eureka（当前注册在eureka的实例）栏目下，点击某些实例，跳转到该实例的/info路径，
显示错误页面，那么应该是该实例的actuator配置错误。
如果许多节点都无法访问，返回401，主要是因为权限的配置不正确
http://blog.csdn.net/u013076044/article/details/60780151
yml中添加management:security:enabled=false
详见actuator项目中的README.md
---
---
####中途总结
目前的项目以及其作用：
* eureka-server:eureka服务注册中心，配置了3个实例，形成一个集群。每个实例中需要配置其他所有实例的路径，以及一个实例名。
* eureka-client:eureka服务（客户端），将自身注册到服务注册中心(eureka-server)。供消费者调用.其中这个项目中写的TestController中的
index()方法就是这个服务提供的服务。这个服务的名字就叫zxzx,并且有2个实例，所以client.getInstances("zxzx")返回的List的size为2，就是
两个服务实例的一些信息。
* ribbon-consumer:使用ribbon进行负载均衡地调用服务的消费者。使用RestTemplate类，访问服务中心对应服务的名字。如果服务有多个实例，就会
自动的根据配置的策略负载均衡。




        
        

