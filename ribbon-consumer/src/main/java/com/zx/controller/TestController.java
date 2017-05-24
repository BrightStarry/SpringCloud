package com.zx.controller;

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

    @GetMapping("/hello")
    public String test(){
        return restTemplate.getForEntity("http://zxzx/hello",String.class).getBody();
    }
}
