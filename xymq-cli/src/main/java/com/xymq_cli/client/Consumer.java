package com.xymq_cli.client;

import com.xymq_cli.handler.ConsumerHandler;
import com.xymq_cli.listener.MessageData;
import com.xymq_cli.listener.MessageListener;
import com.xymq_cli.message.Message;
import com.xymq_cli.util.ResourceUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author 黎勇炫
 * @date 2022年07月11日 15:08
 */
public class Consumer {
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
    private ConsumerHandler consumerHandler;
     /**
       * 日志信息
       */
    private Logger logger = LoggerFactory.getLogger(Consumer.class);

    static{
        host = ResourceUtils.getKey("server.host");
        port = Integer.parseInt(ResourceUtils.getKey("server.port"));
    }

    /**
     * @param destination 队列/主题名
     * @return
     * @author 黎勇炫
     * @create 2022/7/11
     * @email 1677685900@qq.com
     */
    public Consumer(String destination) {
        consumerHandler = new ConsumerHandler(destination);
        try {
            run();
        }catch (Exception e){
            logger.error("消费者初始化异常");
            e.printStackTrace();
        }
    }

    private void run() {

        NioEventLoopGroup clientGroup = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ConsumerlInitializer(consumerHandler));

            // 连接服务器
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            // 监听关闭连接
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
    }

     /**
       * 设置自动签名
       */
    public void setAutoAcknowledge(boolean autoAcknowledge){
        this.consumerHandler.setAutoAcknowledge(autoAcknowledge);
    }

     /**
       * 设置监听器
       */
    public void createListener(MessageListener listener){
        this.consumerHandler.createListener(listener);
    }

    public static void main(String[] args) {
        Consumer consumer = new Consumer("queue");
        consumer.createListener(new MessageListener() {
            @Override
            public void getMessage(MessageData data) {
                System.out.println(data.getMessage());
            }
        });
    }
}
