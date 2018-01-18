#### Spring Cloud Config 分布式配置 服务端
* 导入依赖
>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
		</dependency>

>
* 在启动类上使用注解开启配置服务 @EnableConfigServer,将自己注册到注册中心 @EnableEurekaClient
* yml中如下配置
>
    server:
      port: 8555
    spring:
      application:
        name: config-server
      cloud:
        config:
          server:
            git:
              # git地址
              uri: https://github.com/BrightStarry/SpringCloud.git
              # 仓库中路径
              #search-paths:
              # 帐号,如果是私有的
              #username: 
              # 密码
              #password: 
>
* 在对应个仓库根目录(可通过search-paths指定其他目录)新建config-client-default.yml文件
* 启动服务端,访问 ip:port/config-client-default.yml即可,或者ip:port/config-client/default也可以.

* 其属性文件和访问的url关系映射如下
>   
    /{application}/{profile}[/{label}]
    /{application}-{profile}.yml
    /{label}/{application}-{profile}.yml
    /{application}-{profile}.properties
    /{label}/{application}-{profile}.properties
>

#### Spring Cloud Config 分布式配置 客户端
* 导入依赖
>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>    
>
* 如下代码,测试 从分布式配置服务端的 配置文件中读取test变量
>
    @RestController
    public class ConfigClientApplication {
    	@Value("${test}")
    	private String test;
    	
    	@GetMapping("/")
    	public String test() {
    		return test;
    	}
>
* 注意,需要将application.yml修改为bootstrap.yml  
>
    因为bootstrap属性文件的优先级比applciation优先级高,这样就会先从bootstrap中加载git地址,
    然后去获取git中的分布式配置文件,加载其中内容.甚至端口号也可以配置在上面
    
    然后在bootstrap.yml中配置
    spring:
      application:
        name: config-client
      cloud:
        config:
          label: master
          profile: default
          uri: http://127.0.0.1:8555
    注意,该application:name需要和服务器上对应的配置文件前缀一致.
    注意,虽然bootstrap属性加载优先级高于application,但服务器上对应的配置文件的配置优先级还是高于本地配置的
>

#### 将服务端和客户端都注册到Eureka
* 两个项目都导入依赖
>
		<dependency>
        	<groupId>org.springframework.cloud</groupId>
        	<artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>        
>
* 在服务端和客户端 配置yml中都增加如下配置,并都增加@EnableDiscoveryClient,以将两者注册到注册中心
>
    eureka:
      client:
        service-url:
          defaultZone: http://server1:1111/eureka/,http://server2:1112/eureka/,http://server3:1113/eureka/
>
* 再在客户端的bootstrap.yml,删除之前的注解,增加如下注解,以自动发现分布式配置服务端
>
      cloud:
        config:
          discovery:
            enabled: true
            service-id: config-server
          profile: default
>


#### 实现客户端动态刷新配置
* 也就是在运行时,我提交新的配置文件到git,然后访问actuator提供的/refresh路径
* 导入依赖
>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>    
>
* 修改git上的配置文件内容,然后post请求对应客户端的/refresh接口即可.注意,需要关闭安全管理
>
    management:
      security:
        enabled: false
>
* 该刷新对于已经生成的bean不会进行更改,例如注入一个类的属性中的值,刷新后也不会变化.
* 此时,可以考虑调用/restart接口重启服务,注意,需要增加如下配置启用重启接口
>
    endpoints:
      restart:
        enabled: true
>
* 或者,使用@RefreshScope注解,注解在类和方法上(应该是指@Bean直接的方法),表示重新注入这个bean(已测试,成功.)

* 此外,可以使用Spring Cloud Bus 和 GitHook(当更新git时,它会自动推送消息到指定接口) 实现自动刷新.
