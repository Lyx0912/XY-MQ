package com.xymq.core;

import com.xymq.message.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author 黎勇炫
 * @date 2022年07月09日 16:26
 */

@Component
@Data
@ConfigurationProperties(prefix = "xymq")
public class XymqServer {
    
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
     * 处理读写操作的工作组
     */
    private NioEventLoopGroup ioGroup = new NioEventLoopGroup();
     /**
       * 存储一对一队列消息
       */
    private ConcurrentHashMap<String, LinkedBlockingDeque<String>> map = new ConcurrentHashMap<String, LinkedBlockingDeque<String>>();
     /**
       * 存储一对多消息
       */
    private ConcurrentHashMap<String, LinkedBlockingDeque<String>> topicmap = new ConcurrentHashMap<String, LinkedBlockingDeque<String>>();
     /**
       * 存储延时队列消息
       */
    private ConcurrentHashMap<String, DelayQueue<Message>> delayQueueMap = new ConcurrentHashMap<>();
     /**
       * 存储延时主题消息
       */
    private ConcurrentHashMap<String, DelayQueue<Message>> delayTopicMap = new ConcurrentHashMap<>();
     /**
       * 队列消费者
       */
    private ConcurrentHashMap<String, List<Channel>> sockMap = new ConcurrentHashMap<String, List<Channel>>();
     /**
       * 在线的订阅者
       */
    private ConcurrentHashMap<String, HashMap<Long, Channel>> topicSockMap = new ConcurrentHashMap<String, HashMap<Long, Channel>>();
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

            ChannelFuture sync = server.bind(port).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException("netty服务端启动失败");
        }finally {
            // 关闭两个工作线程组
            bossGroup.shutdownGracefully();
            ioGroup.shutdownGracefully();
        }
    }

}
