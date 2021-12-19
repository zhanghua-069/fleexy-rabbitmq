package com.fleexy.rabbitmq.chapter06;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * 直接交换机——消息接收者
 */
public class ReceiveLogsDirect01 {

    public static final String EXCHANGE_NAME = "direct_logs";
    public static final String charset = "UTF-8";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "console";
        //声明队列
        channel.queueDeclare(queueName, false, false, false, null);
        //一个队列使用不同的routingKey进行绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "info");
        channel.queueBind(queueName, EXCHANGE_NAME, "warning");
        System.out.println("ReceiveLogsDirect01等待接收消息...");
        channel.basicConsume(queueName, true, (consumerTag, message) -> {
            System.out.println("接收到绑定键：" + message.getEnvelope().getRoutingKey() + " ，消息：" + new String(message.getBody(), charset));
        }, consumerTag -> {
        });

    }

}
