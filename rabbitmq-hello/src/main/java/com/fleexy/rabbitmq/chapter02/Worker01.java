package com.fleexy.rabbitmq.chapter02;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.Channel;

/**
 * 消息队列（Work Queues）—— 工作线程
 */
public class Worker01 {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();

        System.out.println("C2消费者等待消费...");
        /**
         * 消费者消费消息，参数如下：
         * 1.消费哪个队列
         * 2.消费成功后是否自动应答（true：自动应答，false：手动应答）
         * 3.消息成功消费后的回调
         * 4.消息消费失败后的回调
         */
        channel.basicConsume(QUEUE_NAME, true, (consumerTag, delivery) -> {
            //消息消费成功后的处理
            System.out.println("获取到消息：" + new String(delivery.getBody()));
        }, consumerTag -> {
            System.out.println(consumerTag + "消息者取消消费接口的回调逻辑...");
        });
    }
}
