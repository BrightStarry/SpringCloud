package com.zx.configclient.amqp;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.StopWatch;

/**
 * author:ZhengXing
 * datetime:2018/1/19 0019 10:16
 * 自定义接收器
 */
//表示该类是mq的监听器,并监听hello队列
@RabbitListener(queues = "hello")
public class CustomReceiver {

	//标识当前对象id
	private final int instance;

	//构造时传入id
	public CustomReceiver(int instance) {
		this.instance = instance;
	}

	/**
	 * 收到消息的处理方法
	 */
	@RabbitHandler
	public void receive(String in) throws InterruptedException {
		//计时器
		StopWatch stopWatch = new StopWatch();
		//开始计时
		stopWatch.start();
		System.out.println("实例:" + instance +" 接收到消息:" + in + " 线程id:" + Thread.currentThread().getId());
		//模拟长时间消费
		doWork(in);
		stopWatch.stop();
		System.out.println("实例:" + instance +" 消费消息完毕:" + in + "消费时常:"+ stopWatch.getTotalTimeSeconds() + " 线程id:" + Thread.currentThread().getId());
	}

	/**
	 * 根据接收到的消息中的 "."字符的个数,暂停对应描述,模拟长时间任务
	 */
	private void doWork(String in) throws InterruptedException {
		for (char ch : in.toCharArray()) {
			if (ch == '.') {
				Thread.sleep(1000);
			}
		}
	}
}
