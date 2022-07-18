package com.xymq_cli.client;

import com.xymq_cli.client.initializer.SubscriberInitializer;
import com.xymq_cli.handler.ConsumerHandler;
import com.xymq_cli.handler.SubscriberHandler;
import com.xymq_cli.listener.MessageData;
import com.xymq_cli.listener.MessageListener;
import com.xymq_cli.util.ResourceUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主题消息订阅者
 * @author 黎勇炫
 * @date 2022年07月17日 14:46
 */
public class Subscriber {

     /**
       * 服务器端地址
       */
    private static String host;
     /**
       * 服务器端口
       */
    private static int port;
    /**
     * 消息处理器
     */
    private SubscriberHandler subscriberHandler;
    /**
     * 当前消费者的通道
     */
    private Channel channel;
    /**
     * 日志信息
     */
    private Logger logger = LoggerFactory.getLogger(Subscriber.class);

    static{
        host = ResourceUtils.getKey("server.host");
        port = Integer.parseInt(ResourceUtils.getKey("server.port"));
    }

    public Subscriber(String destination,long subscriberId) {
        // 将订阅者唯一标识作和目的地为参数传递给处理器
        this.subscriberHandler = new SubscriberHandler(destination,subscriberId);
    }

    public void run(){
        // 订阅者工作组
        NioEventLoopGroup clientGroupS = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientGroupS)
                    .channel(NioSocketChannel.class)
                    .handler(new SubscriberInitializer(subscriberHandler));

            // 连接服务器
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            this.channel = sync.channel();
            // 监听关闭连接
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("订阅者启动失败：{}",e.getMessage());
            e.printStackTrace();
        }finally {
            clientGroupS.shutdownGracefully();
        }
    }

    /**
     * 关闭通道
     * @return void
     * @author 黎勇炫
     * @create 2022/7/12
     * @email 1677685900@qq.com
     */
    public void close(){
        this.channel.close();
    }

    /**
     * 设置监听器
     * @param listener 监听器
     * @return com.xymq_cli.client.Consumer
     * @author 黎勇炫
     * @create 2022/7/12
     * @email 1677685900@qq.com
     */
    public Subscriber createListener(MessageListener listener){
        this.subscriberHandler.createListener(listener);
        return this;
    }

    public static void main(String[] args) {
        Subscriber subscriber = new Subscriber("topic",1);
        subscriber.createListener(new MessageListener() {
            @Override
            public void getMessage(MessageData data) {
                System.out.println(data.getMessage());
            }
        }).run();
    }
}
