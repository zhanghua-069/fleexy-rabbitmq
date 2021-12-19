package com.fleexy.rabbitmq.chapter08;

import com.fleexy.rabbitmq.RabbitmqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列——正常消费者C1（消费正常队列中的消息）
 * 测试3：模拟消息被拒
 */
public class Consumer01_1 {

    //普通队列名称
    public static final String NORMAL_QUEUE = "normal_queue";
    //死信队列名称
    public static final String DEAD_QUEUE = "dead_queue";
    //普通交换机名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    //死信交换机名称
    public static final String DEAD_EXCHANGE = "dead_exchange";

    public static final String CHARSET = "UTF-8";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitmqUtils.getChannel();
        //声明普通交换机和死信交换机，类型为direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        //声明死信队列（死信队列就是一个正常的队列，绑定死信交换机）
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "lisi");

        /**
         * 声明正常队列，绑定正常交换机和死信交换机，参数说明：
         * 1.x-max-length：限制加入queue中的消息条数，是非负整数（遵循先进先出原则，超过10条后面的消息会顶替前面的消息）
         * 2.x-max-length-bytes：与x-max-length一样限制队列的容量（但通过队列的大小bytes来限制），非负整数
         * 3.x-message-ttl：消息存活时间，非负整数值，指定消息在queue中存活的时间（通过x-dead-letter-routing-key和x-dead-letter-exchange生成可延迟的死信队列）
         * 4.x-max-priority：消息优先级，整数值；表示队列应该支持的最大优先级（建议取值1~10，优先级越高消耗资源越多）
         * 5.x-expires：消息存活时间，到期后消息直接消失
         * 6.x-dead-letter-exchange和x-dead-letter-routing-key：指定死信队列的交换机与routingKey
         */
        Map<String, Object> arguments = new HashMap<>();
        //设置正常队列绑定死信队列
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);
        //正常队列绑定死信队列的RoutingKey
        arguments.put("x-dead-letter-routing-key", "lisi");

        //声明正常队列
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "zhangsan");

        System.out.println("C1等待接收消息...");
        //模拟消息被拒，需要关闭自动应答，改为手动应答
        channel.basicConsume(NORMAL_QUEUE, false, (consumerTag, delivery) -> {
            String msg = new String(delivery.getBody(), CHARSET);
            if ("info5".equals(msg)) {
                System.out.println("C1接收到消息：" + msg + " 并拒绝签收该消息");
                /**
                 * requeue设置为 false：代表拒绝消息重新入队，如果队列配置了死信交换机，将会发送到死信队列中
                 */
                channel.basicReject(delivery.getEnvelope().getDeliveryTag(), false);
            } else {
                System.out.println("C1接收到消息：" + msg);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        }, consumerTag -> {});
    }

}
