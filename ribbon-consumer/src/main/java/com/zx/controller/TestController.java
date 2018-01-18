package com.zx.controller;

import com.zx.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by 97038 on 2017-05-21.
 */
@RestController
public class TestController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HelloService helloService;

    @GetMapping("/hello")
    public String test(){
        /**
         * 本来直接使用restTemplate调用，现在把这个调用放到service中去，
         * 以实现Hystrix的容错机制。
         */
//        return restTemplate.getForEntity("http://zxzx/hello",String.class).getBody();
        return helloService.hello();
    }
}
