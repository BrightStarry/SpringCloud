package com.zx.configclient.amqp;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author:ZhengXing
 * datetime:2018/1/19 0019 10:03
 * RabbitMQ配置类
 */
public class AmqpConfig {

	/**
	 * 声明一个队列,可以查看其构造函数,有多的参数可以设置
	 */
	public Queue helloQueue() {
		return new Queue("hello");
	}

	/**
	 * 声明一个匿名队列
	 * 该队列是匿名的,独占的,非持久的,自动删除的.
	 */
	public Queue anonymousQueue1() {
		return new AnonymousQueue();
	}

	/**
	 * 声明一个交换器
	 */
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange("zx.test");
	}

	/**
	 * 将交换器和队列绑定
	 */
	public Binding binding1(Queue anonymousQueue1, FanoutExchange fanoutExchange) {
		//绑定该队列 到 该交换器
		return BindingBuilder.bind(anonymousQueue1).to(fanoutExchange);
	}

	/**
	 * 注入自定义发送器
	 */
	public CustomSender customSender() {
		return new CustomSender();
	}

	/**
	 * 注入自定义接收器1
	 */
	public CustomReceiver customReceiver1() {
		return new CustomReceiver(1);
	}
	/**
	 * 注入自定义接收器2
	 */
	public CustomReceiver customReceiver2() {
		return new CustomReceiver(2);
	}
}
