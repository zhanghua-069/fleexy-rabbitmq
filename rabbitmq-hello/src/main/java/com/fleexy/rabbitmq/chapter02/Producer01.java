package com.fleexy.rabbitmq.chapter02;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * 消息队列（Work Queues）—— 消息发送者
 */
public class Producer01 {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        /**
         * 生成一个队列，参数如下：
         * 1.队列名称
         * 2.队列里面的消息是否持久化（消息默认存储在内存中）
         * 3.队列是否只供一个消息者进行消费，即是否进行共享（设为true时，可以由多个消费者进行消费）
         * 4.是否自动删除：最后一个消费者断开连接后，该队列是否自动删除（设为true，会自动删除）
         * 5.其他参数（如设置延迟队列，死信队列等）
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext() && !(message = scanner.next()).equals("quit")) {
            /**
             * 发送一个消息，参数说明
             * 1.发送到哪个交换机(""代表默交换机)
             * 2.路由的key是哪个（交换机为""时，routingKey即对应队列的名称）
             * 3.其他的参数信息
             * 4.发送消息的消息体
             */
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("发送消息完成:" + message);
        }
        System.exit(0);
    }
}
