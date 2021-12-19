package com.fleexy.rabbitmq.chapter07;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * 主题交换机——消息消费者
 * 队列1主题：*.orange.*
 */
public class ReceiveLogsTopic01 {

    public static final String EXCHANGE_NAME = "topic_logs";
    public static final String charset = "UTF-8";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        String queueName = "Q1";
        //声明队列
        channel.queueDeclare(queueName, false, false, false, null);
        //一个队列使用不同的routingKey进行绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "*.orange.*");
        System.out.println("Q1等待接收消息...");
        channel.basicConsume(queueName, true, (consumerTag, delivery) -> {
            System.out.println("接收队列：" + queueName + "绑定键：" + delivery.getEnvelope().getRoutingKey()
                    + "，消息：" + new String(delivery.getBody(), charset));
        }, consumerTag -> {
        });

    }
}
