#### Eureka Server 服务注册中心


#### 关闭Eureka自我保护,解决不自动剔除已关闭节点的问题
* 服务端配置
>
    eureka:
      server:
        enable-self-preservation: false # 关闭自我保护(开启后和节点间网络频繁故障,会进入保护模式,不再剔除节点)
        eviction-interval-timer-in-ms: 10000 # 清除无效节点间隔
>
* 客户端配置
>
    eureka.instance.lease-renewal-interval-in-seconds	# 检查节点是否到期间隔（默认30秒）
    eureka.instance.lease-expiration-duration-in-seconds # 节点连接不上到期超时时间（默认90秒）
>

#### Eureka Server 服务注册中心
Spring Cloud Eureka 是服务治理框架。
* Eureka Server ：服务端，也就是注册中心。
* Eureka Client ：客户端，处理服务的注册与发现。
---
1. 在pom中导入依赖

        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka-server</artifactId>
		</dependency>
		同时指定版本号
		<spring-cloud.version>Dalston.RELEASE</spring-cloud.version>
并且，如果使用Spring initializr构建项目，会自动多增加如下一段版本依赖
        
        <dependencyManagement>
        		<dependencies>
        			<dependency>
        				<groupId>org.springframework.cloud</groupId>
        				<artifactId>spring-cloud-dependencies</artifactId>
        				<version>${spring-cloud.version}</version>
        				<type>pom</type>
        				<scope>import</scope>
        			</dependency>
        		</dependencies>
        	</dependencyManagement>
---
2. 在默认的Application类中：
@EnableEurekaServer注解，启动服务注册中心；

默认情况下，注册中心（服务端）会将自己作为客户端来尝试注册自己，可以在application中如下配置禁用：
server.port=1111 
eureka.instance.hostname=localhost 
eureka.client.register-with-eureka=false #表示不向注册中心注册自己（也就是禁用该注册中心将自己作为客户端进行注册）
eureka.client.fetch-register=false #是否需要检索服务（注册中心的作用就是维护服务，所以不需要检索）
eureka.client.serviceUrl.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka #服务中心注册地址

eureka.client.serviceUrl.defaultZone，这个配置的作用是(如果eureka.client.register-with-eureka不为false)，
把自己作为一个服务提供者，注册到这个url的服务中心上去。
---
3. 访问默认的http://localhost:port进入eureka服务端管理界面
---
### Eureka Server集群配置
每个注册中心，都在其他的注册中心注册自己
暂定3个节点的集群
1. 在3个配置文件中配置(可在同一个项目中创建application-server1.yml这样的文件3个)
相同部分:这个相当于集群名字，必须相同
>        spring:
>         application:
>            name: eureka-server
然后：

    server:
      port: 1111   #每个节点各自的端点，如果是不同主机的话可以一样
    eureka:
      instance:
        hostname: server3    #该服务主机名
      client:
          service-url:
            #将每个节点在另外的几个节点上注册
            defaultZone: http://server1:1111/eureka/,http://server2:1112/eureka/  
     
如果需要使用ip，而不是主机名来连接的话，需要
eureka.instance.prefer-ip-address: true

---
2. 需要在hosts中增加对应的主机名。并设置ip（本地测试就设置成127.0.0.1 server1这样子3句）
---
3. 使用Maven执行package。将项目打成jar。（idea本身的打成jar有bug，我试了很久）。
然后项目中的target文件中就有了jar。
然后在run中配置，对应的参数 --spring.profiles.active=server1 这样（当然，这是单个项目的测试做法），连续启动3个jar。就有了3个注册中心节点。
打开任意一个注册中心的管理界面，就会发现，其他两个节点已经被当作服务注册在上面了。
这样子服务就会相互同步，实现高可用。



#### Spring Boot Admin  第三方的jar,提供spring cloud的管理界面,包括server(服务)端和client(服务提供者)端
* 遇到了各种版本冲突导致的类或文件无法找到的问题,瞎换了几个版本后无法解决.
>
    然后仔细看了maven中这个spring boot admin依赖的各类框架的版本,确定了它依赖的大致范围是spring boot 1.5.9的
    于是进入spring cloud官网,查看各版本中spring boot的版本号,然后替换上了一个spring boot 版本为1.5.8的.ok
>
* server端
    1. 导入依赖
    >
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-server</artifactId>
            <version>1.5.6</version>
        </dependency>
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-server-ui</artifactId>
            <version>1.5.6</version>
        </dependency>
    >
    2. 在主类,增加@EnableAdminServer注解(和@EnableDiscoveryClient)
    3. 如下配置,可增加安全性,具体参照官方文档
    >
        spring:
          profiles: secure
          boot:
            admin:
              username: "${security.user.name}"       #These two are needed so that the client
              password: "${security.user.password}"   #can register at the protected server api
              client:
                metadata:
                  user.name: "${security.user.name}"          #These two are needed so that the server
                  user.password:  "${security.user.password}" #can access the proteceted client endpoints
    >
    4. 如下配置,指定admin管理页面的路径,官方推荐最好配置,以防止和默认spring cloud路径的冲突
    >
        spring:
          boot:
            admin:
              context-path: /admin        
    >
* client端(包括消费者,或者其他不注册到注册中心的节点也可以通过第二种配置注册到admin)
    * 如果在Client端,启用了@EnableDiscoveryClient注解,可以直接使用.不过必须有依赖
    >
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
    >
    * 如果无效,则尝试如下(应该是可以的,如果不行,确认启动了所有的Eureka注册中心节点.):
    (如果如下和上面的同时启用,会导致在admin中注册两次)
    >
    		<dependency>
    			<groupId>de.codecentric</groupId>
    			<artifactId>spring-boot-admin-starter-client</artifactId>
    			<version>1.5.6</version>
    		</dependency>
    		
    		spring:
              boot:
                admin:
                  url: http://127.0.0.1:1111   # 该url为eureka注册中心地址,可为集群
    >
    * 注意,要在client配置如下,关闭安全保护
    >
        management:
          security:
            enabled: false
    >







