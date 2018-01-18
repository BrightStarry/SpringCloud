package com.zx.controller;

import com.zx.dto.User;
import com.zx.service.TestService;
import com.zx.service.TestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by 97038 on 2017-05-21.
 *
 * 在该接口中. 使用service类去调用 服务提供者 提供的接口, 将结果返回给 调用该接口的 用户
 */
@RestController
public class TestController {

    @Autowired
    private TestServiceImpl testServiceImpl;

    @Autowired
    private TestService testService;

    @GetMapping("/hello")
    public String hello(){
        return testService.invokeHello();
    }

    @GetMapping("/test1")
    public List<User> test1() {
        return testService.invokeTest1(new User("test", "test"));
    }
}
