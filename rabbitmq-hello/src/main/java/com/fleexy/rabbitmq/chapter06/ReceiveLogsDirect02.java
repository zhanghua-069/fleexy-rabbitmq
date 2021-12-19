package com.fleexy.rabbitmq.chapter06;

import cn.hutool.core.io.FileUtil;
import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.io.File;

/**
 * 直接交换机——消息接收者
 */
public class ReceiveLogsDirect02 {

    public static final String EXCHANGE_NAME = "direct_logs";
    public static final String charset = "UTF-8";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        String queueName = "disk";
        channel.queueDeclare(queueName, false, false, false, null);
        //一个队列使用不同的routingKey进行绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "error");
        System.out.println("ReceiveLogsDirect02等待接收消息...");
        channel.basicConsume(queueName, true, (consumerTag, message) -> {
            String msg = "接收到绑定键：" + message.getEnvelope().getRoutingKey() + " ，消息：" + new String(message.getBody(), charset) + "\n";
            FileUtil.appendUtf8String(msg, new File("C:/Users/zhanghua-069/Desktop/rabbitmq_info.txt"));
            System.out.println("错误日志已接收");
        }, consumerTag -> {
        });

    }

}
