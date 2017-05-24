package com.zx.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * Created by 97038 on 2017-05-21.
 */
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
