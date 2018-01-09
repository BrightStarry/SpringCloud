#### Eureka Client 客户端(服务提供者)：向服务中心注册自己
1. 导入依赖

            <dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-eureka</artifactId>
    		</dependency>
---
2. 写一个Controller，从DiscoveryClient获取到服务中心的服务实例
    
    
            @RestController
            public class TestController {
                private final Logger logger = Logger.getLogger(getClass());
                @Autowired
                private DiscoveryClient client;
                @GetMapping("hello")
                public String index(){
                    //根据名字获取服务实例
                    List<ServiceInstance> list = client.getInstances("zxzx");
                    for (ServiceInstance tempServiceInstance : list){
                        logger.info("/hello :hostname:" + tempServiceInstance.getHost() + "--service-id:" + tempServiceInstance.getServiceId());
                    }
                    return "helloWorld";
                }
            }   
---
3. 在Application类中加上注解
@EnableDiscoveryClient :激活DiscoveryClient实现
---
4. application配置文件：
spring.application.name=zxzx
eureka.client.service-url.defaultZone=http://127.0.0.1:1111/eureka/
上面这个地址就是服务中心的注册地址
如果需要注册到集群的注册中心去的话，使用,分隔。例如：
http://server1:1111/eureka/,http://server2:1112/eureka/,http://server3:1113/eureka/
---
5. 先启动服务注册中心(server)，再启动客户端，然后访问http://127.0.0.1:8080/hello就可以看见控制台的输出了
---
##### 如果需要保证该服务的高可用，服务提供者（也就是这个client项目自己）也需要启动多个实例。
本机测试，可以和eureka-server一样，使用jar方式启动，然后配置启动参数 --server.port=8080 .让多个实例的端口不同就可以了。




