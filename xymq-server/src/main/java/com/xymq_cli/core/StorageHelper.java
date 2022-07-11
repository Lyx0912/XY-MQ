package com.xymq_cli.core;

import com.xymq_cli.message.Message;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author 黎勇炫
 * @date 2022年07月10日 14:43
 */
public interface StorageHelper {
    /**
     * 存储消息到队列消息容器
     * @param queueContainer 队列消息容器
     * @param message 消息对象
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void storeQueueMessage(ConcurrentHashMap<String, LinkedBlockingDeque<Message>> queueContainer, Message message);

    /**
     *  存储消息到主题消息容器
     * @param topicContainer 主题消息容器
     * @param message 消息对象
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void storeTopicMessage(ConcurrentHashMap<String, LinkedBlockingDeque<Message>> topicContainer, Message message);

    /**
     * 存储消息到延时队列容器
     * @param delayQueueMap 延时队列容器
     * @param message 消息对象
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void storeDelayMessage(ConcurrentHashMap<String, DelayQueue<Message>> delayQueueMap, Message message);

    /**
     * 存储消息到延时主题消息容器
     * @param delayTopicMap 主题消息容器
     * @param message 消息对象
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void storeDelayTopicMessage(ConcurrentHashMap<String, DelayQueue<Message>> delayTopicMap, Message message);
}
