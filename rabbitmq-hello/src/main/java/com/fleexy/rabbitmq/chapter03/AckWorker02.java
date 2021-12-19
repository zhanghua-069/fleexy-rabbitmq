package com.fleexy.rabbitmq.chapter03;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.Channel;

import java.util.concurrent.TimeUnit;

/**
 * 消息应答 —— 消息消费者
 */
public class AckWorker02 {

    public static final String QUEUE_NAME = "ack_queue";
    public static final String charset = "UTF-8";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();

        System.out.println("C2等待接收消息，处理时间较长...");
        //设置不公平分发
//        int prefetchCount = 1;
        //设置预取值为2
        int prefetchCount = 5;
        channel.basicQos(prefetchCount);
        /**
         * 消费者消费消息，参数如下：
         * 1.消费哪个队列
         * 2.消费成功后是否自动应答（true：自动应答，false：手动应答）
         * 3.消息成功消费后的回调
         * 4.消息消费失败后的回调
         */
        //autoAck设为手动应答
        channel.basicConsume(QUEUE_NAME, false, (consumerTag, delivery) -> {
            try {
                TimeUnit.SECONDS.sleep(20L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("接收到消息：" + new String(delivery.getBody(), charset));
            /**
             * 消息应答，参数说明：
             * 1.消息标识tag
             * 2.false：非批量应答，只应答接收到的那个消息，true：批量应答，对channel中的所有消息进行应答
             */
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }, consumerTag -> {
            System.out.println(consumerTag + "消息者取消消费接口的回调逻辑...");
        });
    }
}
