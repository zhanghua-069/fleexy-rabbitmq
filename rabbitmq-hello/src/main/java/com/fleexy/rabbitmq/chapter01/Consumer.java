package com.fleexy.rabbitmq.chapter01;

import com.rabbitmq.client.*;

/**
 * 定义消息消费者
 */
public class Consumer {

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
        System.out.println("等待接收消息...");
        //推送的消息如何进行消费的接口回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            //消息消费成功后的处理
            System.out.println("获取到消息：" + new String(message.getBody()));
        };
        //取消消费的接口回调，如在消费时队列被删除了
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("消息消费被中断...");
        };
        /**
         * 消费者消费消息，参数如下：
         * 1.消费哪个队列
         * 2.消费成功后是否自动应答（true：自动应答，false：手动应答）
         * 3.消息成功消费后的回调
         * 4.消息消费失败后的回调
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
