package com.fleexy.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * RabbitMQ连接工具类
 */
public class RabbitmqUtils {

    public static Channel getChannel() throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.7");
//        factory.setHost("172.20.10.11");
        factory.setUsername("admin");
        factory.setPassword("1234");
        //获取一个连接
        Connection connection = factory.newConnection();
        //打开一个channel
        //channel实现了自动close接口，会自动关闭，不需要显示关闭
        return connection.createChannel();
    }
}
