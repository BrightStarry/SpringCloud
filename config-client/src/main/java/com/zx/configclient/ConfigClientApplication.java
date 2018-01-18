package com.zx.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RefreshScope
@RestController
@SpringBootApplication
@EnableDiscoveryClient
public class ConfigClientApplication {
	@Value("${test}")
	String test;
	@GetMapping("/")
	public String test() {
		return test;
	}

	public static void main(String[] args) {

		SpringApplication.run(ConfigClientApplication.class, args);
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}
}
