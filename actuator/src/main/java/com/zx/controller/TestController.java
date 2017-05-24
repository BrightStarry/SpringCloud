package com.zx.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 */
@RestController
public class TestController {

    @GetMapping
    public String test(){
        return "This is Test Controller!";
    }
}
