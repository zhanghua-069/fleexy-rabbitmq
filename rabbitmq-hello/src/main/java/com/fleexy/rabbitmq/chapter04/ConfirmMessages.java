package com.fleexy.rabbitmq.chapter04;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.Channel;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式
 * 1.单个确认发布
 * 2.批量确认发布
 * 3.异步确认发布
 */
public class ConfirmMessages {

    //批量发布消息的个数
    public static final int MESSAGE_COUNT = 1000;
    public static final String QUEUE_NAME = "confirm_queue";

    public static void main(String[] args) throws Exception {
        //单个确认发布
//        publishMessageIndividually();//发布1000个单独确认消息，耗时547ms
        //批量确认发布
//        publishMessageBatch();//发布1000个批量确认消息，耗时177ms
        //异步确认发布
        publishMessageAsync();//发布1000个异步发布确认消息，耗时86ms
    }

    /**
     * 单个确认发布
     *
     * @throws Exception
     */
    public static void publishMessageIndividually() throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();

        Instant now = Instant.now();
        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            channel.basicPublish("", QUEUE_NAME, null, (i + "").getBytes());
            //服务端返回 false 或超时时间内未返回，生产者可以消息重发
            if (channel.waitForConfirms())
                System.out.println("消息发送成功");
        }
        System.out.println(String.format("发布%d个单独确认消息，耗时%sms", MESSAGE_COUNT, Duration.between(now, Instant.now()).toMillis()));
    }

    /**
     * 批量确认发布
     *
     * @throws Exception
     */
    public static void publishMessageBatch() throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();

        //批量确认消息大小
        int batchSize = 100;
        //未确认消息个数
        int outstandingMessageCount = 0;
        Instant now = Instant.now();
        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            channel.basicPublish("", QUEUE_NAME, null, (i + "").getBytes());
            //服务端返回 false 或超时时间内未返回，生产者可以消息重发
            outstandingMessageCount++;
            if ((i + 1) % batchSize == 0) {
                channel.waitForConfirms();
                outstandingMessageCount = 0;
            }
        }
        //为了确保还有剩余没有确认消息 再次确认
        if (outstandingMessageCount > 0)
            channel.waitForConfirms();
        System.out.println(String.format("发布%d个批量确认消息，耗时%sms", MESSAGE_COUNT, Duration.between(now, Instant.now()).toMillis()));
    }

    /**
     * 异步确认发布
     *
     * @throws Exception
     */
    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();

        /**
         * 未确认消息表（一个线程安全有序的哈希表，适用于高并发的情况下）
         * 1.将序号与消息进行关联（key为channel中下一个消息的序列号，value为对应的消息）
         * 2.根据序号批量删除消息条目
         * 3.支持高并发（多线程）
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();

        Instant now = Instant.now();
        //消息监听器：监听哪些消息成功了，哪些消息失败了
        /**
         * 消息确认回调函数
         * 1.消息的标记
         * 2.是否批量确认
         *
         * 操作说明：
         * 1.消息确认成功，删除已确认的消息（剩下的就是未确认的消息）
         * 2.消息确认失败，打印未确认的消息的信息
         */
        channel.addConfirmListener((seqNo, multiple) -> {
            if (multiple) {//批量确认消息
                //返回的是小于等于当前序列号的未确认消息，是一个 map
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(seqNo);
                //清除已确认的消息
                confirmed.clear();
            } else {
                //只清除当前序列号的消息
                outstandingConfirms.remove(seqNo);
            }
        }, (seqNo, multiple) -> {
            //未确认的消息
            String message = outstandingConfirms.get(seqNo);
            System.out.println("发布的消息" + message + "未被确认，序列号" + seqNo);
        });
        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "消息" + i;
            /**
             * 1.channel.getNextPublishSeqNo()：获取下一个消息的序列号
             * 2.通过channel的序列号与消息体进行关联
             * 3.初始状态下都是未确认的消息
             */
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        }
        System.out.println(String.format("发布%d个异步发布确认消息，耗时%sms", MESSAGE_COUNT, Duration.between(now, Instant.now()).toMillis()));
    }
}
