package com.zx.service;

import com.zx.dto.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018/1/9 0009 10:08
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
