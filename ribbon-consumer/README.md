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
并通过@LoadBalanced注解开启客户端负载均衡,默认的策略是轮询 


            @Bean
            @LoadBalanced
            RestTemplate restTemplate(){
                return new RestTemplate();
            }
4. 创建TestController,注入成员变量RestTemplate，
然后写一个方法，使用该模版调用服务中心的zxzx服务（eureka-client项目注册在服务中心的服务）：
     @GetMapping("/hello")
        public String test(){
            return restTemplate.getForEntity("http://zxzx/hello",String.class).getBody();
        }
        
* 如下,自定义配置负债均衡策略,使用@LoadBalanced注解开启后,默认为轮询.
>
    /**
     * 配置Ribbon负载均衡配置
     */
    @Configuration
    public class RibbonConfig {
    	/**
    	 * 注册一个 负载均衡策略为bean,自动替换默认的轮询策略
    	 */
    	@Bean
    	public IRule ribbonRule() {
    		//该策略为 随机调用 某个服务提供者节点
    		return new RandomRule();
    	}
    }
>

### Spring Cloud Hystrix 服务容错保护

#### 基本使用
1. 引入依赖
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-hystrix</artifactId>
		</dependency>
2. 在项目Application类中加入注解@EnableCircuitBreaker
    也就是说，开启熔断机制的消费项目，application类总共需要三个注解:
        @SpringBootApplication
        @EnableDiscoveryClient
        @EnableCircuitBreaker
    另外，这三个注解可以直接使用一个注解@SpringCloudApplication来代替
4. service中的方法如下：
        /**
         * 使用restTemplate调用服务端实例，进行容错回调，如果出错，执行{@link #testError()}方法
         * @return
         */
        @HystrixCommand(fallbackMethod = "testError")
        public String test() {
            return restTemplate.getForEntity("http://zxzx/hello",String.class).getBody();
        }
    
        public String testError(){
            return "error";
        }
则如果请求某个服务时，当返回失败，则调用异常回调方法。

#### 同步执行和异步执行  TODO


#### Spring Cloud Feign 声明式服务调用
* feign会自动使用ribbon.无需注解@LoadBalanced
* 首先需要在该服务提供者端配置.
1. 引入依赖
>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-feign</artifactId>
		</dependency>    
>
2. 在Application主类上启用@EnableFeignClients. 
>
    如果使用单一的Eureka作为注册中心,更好的是使用@EnableEurekaClient注解.基本是一样
    EnableFeignClients不过是支持其他一些框架作为注册中心而已.
>
3. 创建一个service接口,在上面注解@FeignClient("client-zx") ,
表明这是一个声明式服务接口.要调用的服务提供者名为 "client-zx" (服务名不区分大小写).也就是eureka-client模块  
并且,其注解要和服务提供者(client)接口上的spring MVC注解保持一致,
如下的test1注解,可以传递@ReqeustBody参数.
>
    /**
     * 使用该类声明式的去调用 服务提供者的 接口
     */
    @FeignClient("client-zx")
    public interface TestService {
    
    	/**
    	 * 调用 服务提供者"client-zx"的/hello接口
    	 */
    	@GetMapping("/hello")
    	String invokeHello();
    
    	/**
    	 * 调用test1接口
    	 */
    	@PostMapping("/test1")
    	List<User> invokeTest1(@RequestBody User user);
    }
>

* !!!既然这个声明式Service类已经注解了其要调用接口的相同注解.  
那么,可以直接将所有声明式service类抽离成一个jar.  
在服务提供者(client)编写对应controller的时候,依赖这个jar,直接继承这些声明式service类.然后重写方法,完成真正的逻辑即可.  
但这样做.可能导致过度耦合.修改controller参数时,必须修改对应声明式service.反之亦然.






