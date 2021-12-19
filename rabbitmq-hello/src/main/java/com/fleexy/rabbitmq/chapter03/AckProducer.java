package com.fleexy.rabbitmq.chapter03;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

/**
 * 消息应答 —— 消息生产者
 */
public class AckProducer {

    public static final String QUEUE_NAME = "ack_queue";
    public static final String charset = "UTF-8";

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
        System.out.println("请输入消息：");
        while (scanner.hasNext() && !(message = scanner.nextLine()).equals("quit")) {
            /**
             * 发送一个消息，参数说明
             * 1.发送到哪个交换机（设为""时，直接发送到队列中），注意：此处只能设为空串，不能设置为null
             * 2.路由的key是哪个（交换机为""时，即对应队列的名称）
             * 3.其他的参数信息
             * 4.发送消息的消息体
             */
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(charset));
            System.out.println("生产者发出消息：" + message);
        }
        System.exit(0);
    }
}
