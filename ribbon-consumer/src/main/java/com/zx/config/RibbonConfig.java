package com.zx.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author:ZhengXing
 * datetime:2018/1/9 0009 14:35
 * 配置Ribbon负载均衡配置
 */
@Configuration
public class RibbonConfig {
	/**
	 * 注册一个 负载均衡策略为bean,自动替换默认的轮询策略
	 */
	@Bean
	public IRule ribbonRule() {
		//该策略为 随机调用 某个服务提供者节点
		return new RandomRule();
	}
}
