package com.fleexy.rabbitmq.chapter08;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.concurrent.TimeUnit;

/**
 * 死信队列——消息发送者
 */
public class Producer {

    //普通交换机名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    public static final String CHARSET = "UTF-8";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        //设置消息的ttl时间（单位：ms）
        AMQP.BasicProperties props = new AMQP.BasicProperties()
                .builder()
                .expiration("10000")
                .build();
        for (int i = 1; i <= 10; i++) {
            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", props, message.getBytes(CHARSET));
            TimeUnit.SECONDS.sleep(2L);
            System.out.println("生产者发送消息：" + message);
        }
    }
}
