package com.xymq_cli.execution;

import com.xymq_cli.constant.Destination;
import com.xymq_cli.constant.MessageConstant;
import com.xymq_cli.core.XymqServer;
import com.xymq_common.message.Message;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 黎勇炫
 * @date 2022年07月10日 12:41
 */
@Component
public class ConsumerExec implements Execution{

    @Autowired
    private XymqServer xymqServer;

    /**
     * 执行操作(消费、推送和签收)
     * @param message
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    @Override
    public void exec(Message message, Channel channel) {
        //如果是消费者，判断消费者是消费队列消息还是订阅主题
        if (message.getDestinationType() == Destination.QUEUE.getDestination()) {
            addConsumer(message, channel);
        } else {
             /**
               * 如果是主题订阅者，就需要先查看是否是之前离线的订阅者(判断标准是订阅者编号)
               * 如果是之前离线的订阅者并且还有未接受到的消息就将之前的消息先推送
               */
            ConcurrentHashMap<String, HashMap<Long, Channel>> subscriberContainer = xymqServer.getSubscriberContainer();
            ConcurrentHashMap<String, HashMap<Long, Channel>> offLineSubscriber = xymqServer.getOffLineSubscriber();
            // 如果离线订阅者容器存在这个主题
            if (offLineSubscriber.containsKey(message.getDestination())) {
                // 获取这个容器
                HashMap<Long, Channel> offLineSubscribers = offLineSubscriber.get(message.getDestination());
                // 离线订阅者容器中是否存在指定的订阅者(messageId)实际上就是订阅者编号
                if (offLineSubscribers.containsKey(message.getMessageId())) {
                    // 将新channel覆盖原来断开连接的channel
                    offLineSubscribers.put(message.getMessageId(), channel);
                } else {
                    // 如果不是离线订阅者就直接存入订阅者容器
                    putSubscriber(message, channel, subscriberContainer);
                }
            } else {
                putSubscriber(message, channel, subscriberContainer);
            }
        }
    }

    /**
     * 将订阅者channel存入容器
     * @param message 消息对象(订阅者中messageId就是订阅者编号)
     * @param channel 通道
     * @param subscriberContainer 订阅者容器
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    private void putSubscriber(Message message, Channel channel, ConcurrentHashMap<String, HashMap<Long, Channel>> subscriberContainer) {
        // 如果容器中已经存在该目的地了就直接存入
        if (subscriberContainer.containsKey(message.getDestination())) {
            subscriberContainer.get(message.getDestination()).put(message.getMessageId(), channel);
        } else {
            // 如果容器中不存在该目的地就新建一个容器在存入
            subscriberContainer.put(message.getDestination(), new HashMap<>());
            subscriberContainer.get(message.getDestination()).put(message.getMessageId(), channel);
        }
    }

    /**
     *
     * @param message 消息对象
     * @param channel 客户端的通道
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    private void addConsumer(Message message, Channel channel) {
        ConcurrentHashMap<String, List<Channel>> consumerContainer = xymqServer.getConsumerContainer();
        // 如果存在这个容器就直接将channel存入容器，否则就新建一个容器
        if (consumerContainer.containsKey(message.getDestination())) {
            consumerContainer.get(message.getDestination()).add(channel);
        } else {
            // 新建容器后存入
            consumerContainer.put(message.getDestination(), new ArrayList<Channel>());
            consumerContainer.get(message.getDestination()).add(channel);
        }
    }

    /**
     * 返回当前策略支持的 消息 类型
     *
     * @return int 消息类型
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    @Override
    public int getType() {
        return MessageConstant.CONSUMER;
    }
}
