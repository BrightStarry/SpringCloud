#### API 网关服务 Zuul
简单理解,可以理解为Nginx,实现对客户端请求的 负载均衡/反向代理/路由转发/路由过滤/熔断保护等

#### 实现
* 导入依赖
>
    <dependency>
    	<groupId>org.springframework.boot</groupId>
    	<artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
    	<groupId>org.springframework.cloud</groupId>
    	<artifactId>spring-cloud-starter-ribbon</artifactId>
    </dependency>
    <dependency>
    	<groupId>org.springframework.cloud</groupId>
    	<artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
    <dependency>
    	<groupId>org.springframework.cloud</groupId>
    	<artifactId>spring-cloud-starter-zuul</artifactId>
    </dependency>
    <dependency>
    	<groupId>org.springframework.cloud</groupId>
    	<artifactId>spring-cloud-starter-hystrix</artifactId>
    </dependency>  
>   
* 在启动类注解上@EnableZuulProxy开启网关服务
* 配置如下,注册网关服务到服务注册中心
>
    server:
      port: 10000
    spring:
      application:
        name: api-gateway
>
* 传统路由方式(转发路由到指定ip:port)(只做如下配置时需要先注释eureka依赖,此时网关服务未注册到注册中心)
>
    zuul:
      routes: # 这个routes是一个map.所以如下的key可以任意.
        api-a-url: # 这是map的 key,string类型,如下是map的value,只一个对象,包含 id/path/serviceId/url属性
          # 将path的**通配后缀和 url的路径相匹配, 例如,请求 api服务路径/api-a/hello,自动转发到127.0.0.1:8080/hello
          path: /api-a/**  
          url: http://127.0.0.1:8080/
>
* 面向服务的路由(转发路由到指定服务名的集群,自动负载均衡(轮询))
>
    zuul:
      routes: # 这个routes是一个map.所以如下的key可以任意.
        api-b-url:
          # 将指定路由和指定服务 映射
          path: /api-b/**
          serviceId: client-zx
    # 将网关服务也注册到注册中心
    eureka:
      client:
        service-url: 
          defaultZone: http://server1:1111/eureka/,http://server2:1112/eureka/,http://server3:1113/eureka/
>
* 此时,访问 网关ip:port/api-b/hello,即可自动负载均衡轮询访问到client-zx服务的/hello路径

#### 过滤器
* 定义如下类,可以直接通过@Component注入,或通过@Bean注入
>
    /**
     * author:ZhengXing
     * datetime:2018/1/18 0018 10:04
     */
    @Component
    public class CustomFilter extends ZuulFilter {
    	//过滤器类型,此处pre表示在请求路由前.
    	@Override
    	public String filterType() {
    		return "pre";
    	}
    	//路由器顺序,有多个时
    	@Override
    	public int filterOrder() {
    		return 0;
    	}
    	//判断该过滤器是否需要执行.
    	@Override
    	public boolean shouldFilter() {
    		return true;
    	}
    	//具体过滤逻辑
    	@Override
    	public Object run() {
    		//获取当前请求上下文
    		RequestContext context = RequestContext.getCurrentContext();
    		//获取请求.
    		HttpServletRequest request = context.getRequest();
    		System.out.println("请求路径" + request.getRequestURL());
    
    		try {
    			//如果不包含token,就直接拦截该请求.
    			if(StringUtils.isEmpty(ServletRequestUtils.getStringParameter(request,"token"))){
    				//表示过滤该请求,不对其进行路由.
    				context.setSendZuulResponse(false);
    				//设置返回码
    				context.setResponseStatusCode(401);
    				//设置返回内容
    				context.setResponseBody("xxx");
    				return null;
    			}
    
    		} catch (ServletRequestBindingException e) {
    			e.printStackTrace();
    		}
    		return null;
    	}
    }
>