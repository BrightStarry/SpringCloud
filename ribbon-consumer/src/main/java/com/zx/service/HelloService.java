package com.zx.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by 97038 on 2017-06-05.
 */
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
