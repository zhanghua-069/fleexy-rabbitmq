package com.fleexy.rabbitmq.chapter06;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * 直接交换机 —— 消息发送者
 */
public class EmitLogDirect {

    //交换机名称
    public static final String EXCHANGE_NAME = "direct_logs";
    public static final String charset = "UTF-8";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        /**
         * 声明一个exchange，参数说明：
         * 1.exchange的名称
         * 2.exchange的类型
         */
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String message;
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入消息：");
        while (scanner.hasNext() && !(message = scanner.nextLine()).equals("quit")) {
            //通过exchange发送消息，routingKey使用空字符串（发送到队列时，默认使用队列名作为routingKey）
            channel.basicPublish(EXCHANGE_NAME, "error", null, message.getBytes(charset));
            System.out.println("生产者发出消息：" + message);
        }
        System.exit(0);
    }
}
