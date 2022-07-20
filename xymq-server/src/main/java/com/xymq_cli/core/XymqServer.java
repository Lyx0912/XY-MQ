package com.xymq_cli.core;

import com.alibaba.fastjson.JSON;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.xymq_cli.constant.Destination;
import com.xymq_cli.constant.ServerConstant;
import com.xymq_common.message.Message;
import com.xymq_common.protocol.MessageUtils;
import com.xymq_common.protocol.Protocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author 黎勇炫
 * @date 2022年07月09日 16:26
 */

@Component
@Data
@ConfigurationProperties(prefix = "xymq")
public class XymqServer {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private LevelDb levelDb;
    @Autowired
    private ClientManager clientManager;

    private Logger logger = LoggerFactory.getLogger(XymqServer.class);

    /**
     * 服务端口号
     */
    private int port;
    /**
     * 服务端可连接队列
     */
    private int backLog;
    /**
     * 处理连接请求的工作组
     */
    private NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    /**
     * 1对1消息队列实施轮询策略
     */
    private int queueIndex = 0;
    /**
     * 处理读写操作的工作组
     */
    private NioEventLoopGroup ioGroup = new NioEventLoopGroup();
    /**
     * 存储一对一队列消息
     */
    private ConcurrentHashMap<String, LinkedBlockingDeque<Message>> queueContainer = new ConcurrentHashMap<String, LinkedBlockingDeque<Message>>();
    /**
     * 存储一对多消息
     */
    private ConcurrentHashMap<String, LinkedBlockingDeque<Message>> topicContainer = new ConcurrentHashMap<String, LinkedBlockingDeque<Message>>();
    /**
     * 存储延时队列消息
     */
    private ConcurrentHashMap<String, DelayQueue<Message>> delayQueueContainer = new ConcurrentHashMap<>();
    /**
     * 存储延时主题消息
     */
    private ConcurrentHashMap<String, DelayQueue<Message>> delayTopicContainer = new ConcurrentHashMap<>();
    /**
     * 队列消费者
     */
    private ConcurrentHashMap<String, List<Channel>> consumerContainer = new ConcurrentHashMap<String, List<Channel>>();
    /**
     * 在线的订阅者
     */
    private ConcurrentHashMap<String, HashMap<Long, Channel>> subscriberContainer = new ConcurrentHashMap<String, HashMap<Long, Channel>>();
    /**
     * 存储离线的订阅者
     */
    private ConcurrentHashMap<String, HashMap<Long, Channel>> offLineSubscriber = new ConcurrentHashMap<String, HashMap<Long, Channel>>();
    /**
     * 存储离线订阅消息，K为客户端id，list存放消息
     */
    private ConcurrentHashMap<Long, ArrayList<Message>> offLineTopicMessage = new ConcurrentHashMap<Long, ArrayList<Message>>();

    /**
     * 服务端初始化工作
     *
     * @return void
     * @author 黎勇炫
     * @create 2022/7/9
     * @email 1677685900@qq.com
     */
    public void init() {
        ServerBootstrap server = new ServerBootstrap();
        try {
            server.group(bossGroup, ioGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, backLog)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ServerInitializer());

            ChannelFuture sync = server.bind(port);
            // 还原数据
            recoveryMessage();
            // 开始推送消息
            sendMessageToClients();
            sendDelayMessageToClient();
            sendMessageToSubscriber();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("netty服务端启动失败");
        } finally {
            // 关闭两个工作线程组
            bossGroup.shutdownGracefully();
            ioGroup.shutdownGracefully();
        }
    }

    /**
     * 发送消息到消费者
     *
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    public void sendMessageToClients() {
        // 异步执行，遍历队列消息容器
        CompletableFuture.runAsync(() -> {
            try {
                while (!bossGroup.isShutdown()) {
                    // 遍历整个队列容器
                    for (Map.Entry<String, LinkedBlockingDeque<Message>> entry : queueContainer.entrySet()) {
                        // key就是队列名
                        String key = entry.getKey();
                        LinkedBlockingDeque<Message> queue = entry.getValue();
                        // 当消息队列中有数据并且该队列存在消费者，就调用线程池，负责为该队列推送消息
                        while (queue.size() > 0 && consumerContainer.containsKey(key)) {
                            if (consumerContainer.get(key).size() != 0) {
                                Channel channel = getChannel(consumerContainer.get(key));
                                // 只有连接在活跃状态下才开始推送消息
                                if (channel.isActive()) {
                                    // 发送消息到未断开连接的消费者
                                    Message message = queue.poll();
                                    MessageUtils.message2Protocol(message);
                                    channel.writeAndFlush(MessageUtils.message2Protocol(message));
                                }
                            }
                        }
//                        if ((queue.size() > 0 && consumerContainer.containsKey(key))) {
//                            CompletableFuture.runAsync(() -> {
//                                while (queue.size() > 0 && consumerContainer.containsKey(key)) {
//                                    if (consumerContainer.get(key).size() != 0) {
//                                        Channel channel = getChannel(consumerContainer.get(key));
//                                        // 只有连接在活跃状态下才开始推送消息
//                                        if (channel.isActive()) {
//                                            // 发送消息到未断开连接的消费者
//                                            Message message = queue.poll();
//                                            MessageUtils.message2Protocol(message);
//                                            channel.writeAndFlush(MessageUtils.message2Protocol(message));
//                                        }
//                                    }
//                                }
//                            }, taskExecutor);
//                        }
                    }
                }
            } catch (Exception e) {
                logger.error("消息推送失败");
            }
        }, taskExecutor);
    }

    /**
     * 发送延时消息到消费者
     *
     * @return void
     * @author 黎勇炫
     * @create 2022/7/16
     * @email 1677685900@qq.com
     */
    public void sendDelayMessageToClient() {
        CompletableFuture.runAsync(() -> {
            try {
                while (!bossGroup.isShutdown()) {
                    for (Map.Entry<String, DelayQueue<Message>> entry : delayQueueContainer.entrySet()) {
                        String key = entry.getKey();
                        DelayQueue<Message> queue = entry.getValue();
                        if (queue.size() > 0 && consumerContainer.containsKey(key)) {
                            CompletableFuture.runAsync(()->{
                                while(queue.size() > 0 && consumerContainer.containsKey(key)){
                                    if (consumerContainer.get(key).size() != 0) {
                                        Channel channel = getChannel(consumerContainer.get(key));
                                        if (channel.isActive()) {
                                            /*
                                             * 发送消息到未断开连接的消费者
                                             * */
                                            Message message = null;
                                            try {
                                                message = queue.take();
                                                channel.writeAndFlush(MessageUtils.message2Protocol(message));
                                            } catch (InterruptedException e) {
                                                logger.error("消息{}推送时发生异常",message.getMessageId());
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            },taskExecutor);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        },taskExecutor);
    }

    /**
     * 发布消息到订阅了的客户端
     * @return void
     * @author 黎勇炫
     * @create 2022/7/17
     * @email 1677685900@qq.com
     */
    public void sendMessageToSubscriber() {
        //判断是否所有消费者都已经消费，如果都消费了就从数据库中删除
        CompletableFuture.runAsync(()->{
            try {
                while (!bossGroup.isShutdown()) {
                    for (Map.Entry<String, LinkedBlockingDeque<Message>> entry : topicContainer.entrySet()) {
                        String key = entry.getKey();
                        LinkedBlockingDeque<Message> topicQueue = entry.getValue();
                        // 假如某个主题容器有消息并且有订阅者在线
                        if (topicQueue.size() > 0 && subscriberContainer.containsKey(key)) {
                            CompletableFuture.runAsync(()->{
                                while(topicQueue.size() > 0 && subscriberContainer.containsKey(key)){
                                    if (subscriberContainer.get(key).size() != 0) {
                                        Message message = topicQueue.poll();
                                        // 根据目的地取出指定map。key为订阅者id，value为订阅者的channel
                                        HashMap<Long, Channel> subscribers = subscriberContainer.get(message.getDestination());
                                        // 遍历整个订阅者容器，向每一个订阅者推送消息
                                        for (Map.Entry<Long, Channel> longSocketChannelEntry : subscribers.entrySet()) {
                                            Long clientId = longSocketChannelEntry.getKey();
                                            Channel subscriber = longSocketChannelEntry.getValue();
                                            // 如果当前订阅者在线就推送消息
                                            if (subscriber.isActive()) {
                                                // 推送消息
                                                subscriber.writeAndFlush(MessageUtils.message2Protocol(message));
                                            } else {
                                                // 如果当前订阅者已经离线，就将消费者的id和消息存储起来
                                                // 假如已经有对应的主题离线容器，就直接将channel和订阅者编号存入容器
                                                if (offLineSubscriber.containsKey(key)) {
                                                    offLineSubscriber.get(key).put(clientId, subscriber);
                                                } else {
                                                    // 如果不存在离线订阅者容器就创建容器在channel和订阅者编号存入容器
                                                    offLineSubscriber.put(key, new HashMap<Long, Channel>());
                                                    offLineSubscriber.get(key).put(clientId, subscriber);
                                                }
                                                // offLineTopicMessage中key是订阅者编号，value是未推送消息的集合
                                                // 如果这个离线订阅者已经有未读消息就直接将消息存入
                                                if (offLineTopicMessage.containsKey(clientId)) {
                                                    offLineTopicMessage.get(clientId).add(message);
                                                } else {
                                                    // 刚离线的订阅者就新建容器
                                                    offLineTopicMessage.put(clientId, new ArrayList<>());
                                                    offLineTopicMessage.get(clientId).add(message);
                                                }
                                            }
                                        }
                                        // 推送完从leveldb中删除消息
                                        levelDb.deleteMessageBean(message.getMessageId());
                                    }
                                }
                            },taskExecutor);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("推送主题消息时发生异常");
                e.printStackTrace();
            }
        },taskExecutor);
    }

    /**
     * 取模算法获取channel(一对一的队列用轮询策略)
     *
     * @param channels 通道列表
     * @return io.netty.channel.Channel
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    private Channel getChannel(List<Channel> channels) {
        queueIndex = queueIndex % channels.size();
        Channel channel = channels.get(queueIndex);
        queueIndex++;
        return channel;
    }

    /**
     * 系统启动时要从leveldb中读取消息，将所有的消息重新读到缓冲中
     * 假如时延时队列的消息就要重新判断消息，如果时已经过期的消息就不读到内存中了，否则就重新计算过期事件放入内存
     *
     * @return void
     * @author 黎勇炫
     * @create 2022/7/12
     * @email 1677685900@qq.com
     */
    public void recoveryMessage() {
        List<String> keys = levelDb.getKeys();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (!key.equals("offLineSubscriber") && !key.equals("offLineTopicMessage")) {
                Message message = levelDb.getMessageBean(key);
                if (null != message) {
                    if (message.getDestinationType() == (Destination.QUEUE.getDestination())) {
                        if (queueContainer.containsKey(message.getDestination())) {
                            queueContainer.get(message.getDestination()).offer(message);
                        } else {
                            queueContainer.put(message.getDestination(), new LinkedBlockingDeque<>());
                            queueContainer.get(message.getDestination()).offer(message);
                        }
                    }
                }
            } else {
                switch (key) {
                    case ServerConstant.OFFLINE_MESSAGE_TOPIC:
                        offLineTopicMessage = levelDb.getOffLineMessage();
                        break;
                    case ServerConstant.OFFLINE_SUBSCRIBER:
                        offLineSubscriber = levelDb.getOffLineSubscriber();
                        break;
                    default:
                        //删除没有意义的数据
                        levelDb.deleteMessageBean(Long.parseLong(key));
                        iterator.remove();
                        break;
                }
            }
        }
        System.out.println("server started!");
    }

    /**
     * 清理离线客户端
     * @return void
     * @author 黎勇炫
     * @create 2022/7/19
     * @email 1677685900@qq.com
     */
    @Scheduled(cron = "0/1 * * * * ? ")
    public void clean(){
        clientManager.clean(consumerContainer);
    }

    /**
     * 存储离线客户端和离线消息
     */
    @Scheduled(cron = "0/1 * * * * ? ")
    public void storeOfflineData(){
        clientManager.storeOfflineData(offLineTopicMessage,offLineSubscriber);
    }

}
