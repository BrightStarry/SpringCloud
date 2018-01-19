package com.zx.configclient.amqp;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * author:ZhengXing
 * datetime:2018/1/19 0019 10:08
 * 自定义发送者
 */
public class CustomSender {

	public RabbitTemplate rabbitTemplate;

	private Queue helloQueue;


	//定时发送
	public void send() {
		for (int i = 0; i < 100; i++) {
			String m = "xxx" + ".....";
			rabbitTemplate.convertAndSend(helloQueue.getName(),m);
			System.out.println("发送:" + m);
		}
	}
}
