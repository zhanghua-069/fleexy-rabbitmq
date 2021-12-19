package com.fleexy.rabbitmq.chapter05;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * 交换机-扇出（fanout）模式
 * 消息接收者
 */
public class ReceiveLogs01 {

    //交换机名称
    public static final String EXCHANGE_NAME = "logs";
    public static final String charset = "UTF-8";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //声明一个交换机，类型为fanout
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        /**
         * 声明一个临时队列，队列的名称是随机的
         * 当最后一个消费者断开与队列的连接时，该队列会自动删除
         */
        String queueName = channel.queueDeclare().getQueue();
        //绑定交换机与临时队列，routingKey（也称binding key）为空字符串
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        System.out.println("等待接收消息，把接收到的消息打印在控制台...");
        channel.basicConsume(queueName, true, (consumerTag, message) -> {
            System.out.println("控制台接收到消息：" + new String(message.getBody(), charset));
        }, consumerTag -> {
        });
    }
}
