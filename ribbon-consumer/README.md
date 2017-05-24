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

