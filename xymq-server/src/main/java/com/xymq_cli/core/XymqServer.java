package com.xymq_cli.core;

import com.alibaba.fastjson.JSON;
import com.xymq_cli.message.Message;
import com.xymq_cli.protocol.Protocol;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private ConcurrentHashMap<Long, ArrayList<String>> offLineTopicMessage = new ConcurrentHashMap<Long, ArrayList<String>>();

    /**
     * 服务端初始化工作
     * @return void
     * @author 黎勇炫
     * @create 2022/7/9
     * @email 1677685900@qq.com
     */
    public void init(){
        ServerBootstrap server = new ServerBootstrap();
        try {
            server.group(bossGroup,ioGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,backLog)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ServerInitializer());

            ChannelFuture sync = server.bind(port);
            // 开始推送消息
            sendMessageToClients();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("netty服务端启动失败");
        }finally {
            // 关闭两个工作线程组
            bossGroup.shutdownGracefully();
            ioGroup.shutdownGracefully();
        }
    }

    /**
     * 发送消息到消费者
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
                    System.out.println(queueContainer.size());
                    for (Map.Entry<String, LinkedBlockingDeque<Message>> entry : queueContainer.entrySet()) {
                        // key就是队列名
                        String key = entry.getKey();
                        LinkedBlockingDeque<Message> queue = entry.getValue();
                        // 当消息队列中有数据并且该队列存在消费者，就调用线程池，负责为该队列推送消息
                        System.out.println(queue);
                        if((queue.size() > 0 && consumerContainer.containsKey(key))){
                            CompletableFuture.runAsync(()->{
                                while (queue.size() > 0 && consumerContainer.containsKey(key)) {
                                    if (consumerContainer.get(key).size() != 0) {
                                        Channel channel = getChannel(consumerContainer.get(key));
                                        // 只有连接在活跃状态下才开始推送消息
                                        if (channel.isActive()) {
                                            // 发送消息到未断开连接的消费者
                                            Message message = queue.poll();
                                            byte[] content = JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8);
                                            Protocol protocol = new Protocol(content.length,content);
                                            channel.writeAndFlush(protocol);
                                        }
                                    }
                                }
                            },taskExecutor);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("消息推送失败");
            }
        },taskExecutor);
    }

    /**
     * 取模算法获取channel(一对一的队列用轮询策略)
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


}
