package com.xymq_cli.client;

import com.xymq_cli.client.initializer.ConsumerlInitializer;
import com.xymq_cli.handler.ConsumerHandler;
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
       * 当前消费者的通道
       */
    private Channel channel;
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
    }

    private void run() {

        // 检测消息监听器是否已经启动
        if(this.consumerHandler.getMessageListener() == null){
            throw new NullPointerException("未设置消息监听器");
        }
        NioEventLoopGroup clientGroupC = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientGroupC)
                    .channel(NioSocketChannel.class)
                    .handler(new ConsumerlInitializer(consumerHandler));

            // 连接服务器
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            this.channel = sync.channel();
            // 监听关闭连接
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 关闭工作组
            clientGroupC.shutdownGracefully();
        }
    }

     /**
      * 设置自动签名
      * @param autoAcknowledge 设置自动签名
      * @return void
      * @author 黎勇炫
      * @create 2022/7/12
      * @email 1677685900@qq.com
      */
    public void setAutoAcknowledge(boolean autoAcknowledge){
        this.consumerHandler.setAutoAcknowledge(autoAcknowledge);
    }

     /**
      * 设置监听器
      * @param listener 监听器
      * @return com.xymq_cli.client.Consumer
      * @author 黎勇炫
      * @create 2022/7/12
      * @email 1677685900@qq.com
      */
    public Consumer createListener(MessageListener listener){
        this.consumerHandler.createListener(listener);
        return this;
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
       * 消费者示例
       */
    public static void main(String[] args) {
        // 指定‘queue’队列
        Consumer consumer = new Consumer("queue");
        // 构建监听器
        consumer.createListener(new MessageListener() {
            @Override
            public void getMessage(MessageData data) {
                // 监听到消息会进入MessageListener今天器中
                System.out.println(data.getMessage());
            }
        }).run();
    }
}
