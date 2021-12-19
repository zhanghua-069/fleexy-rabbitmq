package com.fleexy.rabbitmq.chapter01;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 简单生产者demo
 */
public class Producer {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.7");
        factory.setUsername("admin");
        factory.setPassword("1234");
        //获取一个连接
        Connection connection = factory.newConnection();
        //打开一个channel
        //channel实现了自动close接口，会自动关闭，不需要显示关闭
        Channel channel = connection.createChannel();
        /**
         * 生成一个队列，参数如下：
         * 1.队列名称
         * 2.队列里面的消息是否持久化（消息默认存储在内存中）
         * 3.队列是否只供一个消息者进行消费，即是否进行共享（设为true时，可以由多个消费者进行消费）
         * 4.是否自动删除：最后一个消费者断开连接后，该队列是否自动删除（设为true，会自动删除）
         * 5.其他参数（如设置延迟队列，死信队列等）
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "hello world";
        /**
         * 发送一个消息，参数说明
         * 1.发送到哪个交换机(""代表默交换机)
         * 2.路由的key是哪个（交换机为""时，routingKey即对应队列的名称）
         * 3.其他的参数信息
         * 4.发送消息的消息体
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("消息发送完毕");
    }
}
