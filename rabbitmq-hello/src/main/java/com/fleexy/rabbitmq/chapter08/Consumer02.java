package com.fleexy.rabbitmq.chapter08;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * 死信队列——死信队列消费者
 */
public class Consumer02 {

    //死信队列名称
    public static final String DEAD_QUEUE = "dead_queue";
    //死信交换机名称
    public static final String DEAD_EXCHANGE = "dead_exchange";

    public static final String CHARSET = "UTF-8";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //声明死信交换机，类型为direct
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //声明死信队列（死信队列就是一个正常的队列，绑定死信交换机）
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "lisi");

        System.out.println("C2等待接收消息...");
        channel.basicConsume(DEAD_QUEUE, true, (consumerTag, message) -> {
            System.out.println("C2接收到消息：" + new String(message.getBody(), CHARSET));
        }, consumerTag -> {});
    }
}
