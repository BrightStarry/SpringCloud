package com.zx.controller;

import com.zx.dto.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;


/**
 * Created by 97038 on 2017-05-21.
 */
@RestController
public class TestController {
    private final Logger logger = Logger.getLogger(getClass());
    @Autowired
    private DiscoveryClient client;


    @GetMapping("/hello")
    public String hello(){
        //根据名字获取所有服务实例
        List<ServiceInstance> list = client.getInstances("client-zx");
        for (ServiceInstance tempServiceInstance : list){
            logger.info("/hello :hostname:" + tempServiceInstance.getHost() + "--service-id:" + tempServiceInstance.getServiceId());
        }
        return "helloWorld";
    }

    @PostMapping("/test1")
    public List<User> test1(@RequestBody User user) {
        System.out.println(user);
        return Arrays.asList(new User("a", "a"), new User("b", "b"));
    }
}
