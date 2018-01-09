package com.zx.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * author:Administrator
 * datetime:2017/10/30 0030 09:31
 * 使用restTemplate调用服务提供者,非声明式
 */
@Service
public class TestServiceImpl {
    @Autowired
    private RestTemplate restTemplate;

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
}
