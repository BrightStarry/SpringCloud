#### Eureka服务消费者,使用Ribbon实现对服务消费的负载均衡
例如之前配置的eureka-client这个服务提供者，我们可以开启多个该实例，然后使用这个项目，对服务提供者的多个实例进行负载均衡.
1. pom依赖：
    
    
            <dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-eureka</artifactId>
    		</dependency>
    		<dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-ribbon</artifactId>
    		</dependency>
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-web</artifactId>
    		</dependency>
    		
2. 在Application类中使用注解 
@EnableDiscoveryClient
让该项目成为客户端，获得服务发现的能力。
3. 在Application类中创建RestTemplate（Spring提供的用来访问REST资源的bean，好像类似HttpClient），
并通过@LoadBalanced注解开启客户端负载均衡:


            @Bean
            @LoadBalanced
            RestTemplate restTemplate(){
                return new RestTemplate();
            }
4. 创建TestController,注入成员变量RestTemplate，
然后写一个方法，使用该模版调用服务中心的zxzx服务（eureka-client项目注册在服务中心的服务）：
---
---
#### 使用SpringCloudHystrix实现服务容错保护
1. pom依赖


            <dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-hystrix</artifactId>
    		</dependency>
    		
2. 在Application类中使用注解
@EnableCircuitBreaker
开启断路器功能。
此外，如果直接使用@SpringCloudApplication注解，将自动包含了
@SpringBootApplication、
@EnableDiscoveryClient、
@EnableCircuitBreaker
这三个注解

3. 创建一个service


    @Service
    public class HelloService {
        @Autowired
        private RestTemplate restTemplate;
        /**
         * 如果该方法中的调用出现错误，则执行回调方法
         */
        @HystrixCommand(fallbackMethod = "helloFallback")
        public String hello(){
            return restTemplate.getForEntity("http://zxzx/hello",String.class).getBody();
        }
    
        public String helloFallback(){
            return "error";
        }
    }
主要作用就是，使用@HystrixCommand注解开启容错命令，如果错误，就执行回调方法。
所以，此时，如果开启两个该服务实例，然后关闭一个。再使用consumer轮询访问。
那么，一旦访问到关闭的这个实例，就会直接返回error。
此外，除了关闭服务实例。服务超时也会调用回调函数，默认的超时时间为2s。

