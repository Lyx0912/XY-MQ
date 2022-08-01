package com.xymq_cli.client;

import com.xymq_cli.client.initializer.ProducerlInitializer;
import com.xymq_cli.constant.Destination;
import com.xymq_cli.constant.MessageType;
import com.xymq_cli.handler.ProducerHandler;
import com.xymq_cli.util.ResourceUtils;
import com.xymq_common.message.Message;
import com.xymq_common.protocol.MessageUtils;
import com.xymq_common.protocol.Protocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author 黎勇炫
 * @date 2022年07月11日 15:09
 */
public class Producer {
    /**
     * 服务器端地址
     */
    private static String host;
    /**
     * 服务器端口
     */
    private static int port;
    /**
     * 生产者处理器
     */
    private ProducerHandler producerHandler;
    /**
     * 客户端通道
     */
    private Channel channel;
    /**
     * 日志
     */
    private NioEventLoopGroup clientGroup = new NioEventLoopGroup();
    private Logger logger = LoggerFactory.getLogger(Producer.class);

    static {
        host = ResourceUtils.getKey("server.host");
        port = Integer.parseInt(ResourceUtils.getKey("server.port"));
    }

    /**
     * 初始化生产者
     *
     * @return
     * @author 黎勇炫
     * @create 2022/7/13
     * @email 1677685900@qq.com
     */
    public Producer() {
        producerHandler = new ProducerHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ProducerlInitializer(producerHandler));
            // 连接服务器
            ChannelFuture sync = bootstrap.connect(host, port).sync();
            this.channel = sync.channel();
        } catch (InterruptedException e) {
            clientGroup.shutdownGracefully();
            e.printStackTrace();
        }
    }

    /**
     * 发送队列消息，需要传入消息内容以及队列名称
     */
    public void sendMsg(String content, String destinationName) throws InterruptedException {
        Message message = new Message(null, MessageType.PRIVODER.getType(), content, destinationName, Destination.QUEUE.getDestination(), false, 0, TimeUnit.SECONDS);
        Protocol protocol = MessageUtils.message2Protocol(message);
        channel.writeAndFlush(protocol);
    }

    /**
     * 发送延时消息，需要传入消息内容、队列名称、延迟数以及延迟单位
     *
     * @param content         消息内容
     * @param destinationName 目的地
     * @param delay           延迟数
     * @param timeUnit        延迟单位
     * @return void
     * @author 黎勇炫
     * @create 2022/7/16
     * @email 1677685900@qq.com
     */
    public void sendDelayMessage(String content, String destinationName, long delay, TimeUnit timeUnit) {
        Message message = new Message(null, MessageType.PRIVODER.getType(), content,destinationName,Destination.QUEUE.getDestination(), false,delay, timeUnit);
        this.channel.writeAndFlush(MessageUtils.message2Protocol(message));
    }

    /**
     * 发布主题消息，需要传入消息内容和主题名称
     * @param content    消息内容
     * @param destinationName i
     * @return void
     * @author 黎勇炫
     * @create 2022/7/13
     * @email 1677685900@qq.com
     */
    public void publish(String content, String destinationName) {
        Message message = new Message(null, MessageType.PRIVODER.getType(), content,destinationName,Destination.TOPIC.getDestination(),false,0,null);
        this.channel.writeAndFlush(MessageUtils.message2Protocol(message));
    }

    /**
     * 发布延迟主题消息，需要传入消息内容、主题名称、延迟数和延迟单位
     *
     * @param content         消息内容
     * @param destinationName 消息目的地
     * @param delay           延迟数
     * @param timeUnit        延迟单位
     * @return void
     * @author 黎勇炫
     * @create 2022/7/13
     * @email 1677685900@qq.com
     */
    public void publishDelayTopicMessage(String content, String destinationName, long delay, TimeUnit timeUnit) {
        Message message = new Message(null, MessageType.PRIVODER.getType(), content,destinationName,Destination.TOPIC.getDestination(),false,delay,timeUnit);
        channel.writeAndFlush(MessageUtils.message2Protocol(message));
    }

    /**
     * 发送插队消息，该方法可以将一条消息放入一个队列的队列头，将会被优先消费
     *
     * @param content         消息内容
     * @param destinationName 消息目的地
     * @return void
     * @author 黎勇炫
     * @create 2022/7/13
     * @email 1677685900@qq.com
     */
    public void sendPriorityMessage(String content, String destinationName) {
        Message message = new Message(null, MessageType.PRIVODER.getType(), content,destinationName, Destination.QUEUE.getDestination(),true,0,TimeUnit.MILLISECONDS);
        channel.writeAndFlush(MessageUtils.message2Protocol(message));
    }

    /**
     * 等待socket缓冲区的数据都被消费才关闭
     *
     * @return void
     * @author 黎勇炫
     * @create 2022/7/13
     * @email 1677685900@qq.com
     */
    public void close() {
        channel.close().channel();
        this.clientGroup.shutdownGracefully();
    }

    public Channel getChannel(){
        return this.channel;
    }


     /**
       * 生产者实例
       */
    public static void main(String[] args) throws InterruptedException {
        // 创建生产者
        Producer producer = new Producer();
//        producer.publishDelayTopicMessage("你好，延时30s","topic",30,TimeUnit.SECONDS);
        // 推送普通的队列消息
        for (int i = 0; i < 100000; i++) {
            producer.sendMsg("你好，我是队列消息"+i,"queue");
        }
//        Thread.sleep(300);
        // 推送主题消息
//        producer.publish("你好，我是主题消息","topic");
//        // 推送延迟消息，设置延迟数和单位，消息会在5分钟后推送给消费者
//        producer.sendDelayMessage("你好，我是延时队列消息","queueDelayM",5,TimeUnit.SECONDS);
//        // 推送延迟主题消息
//        producer.sendDelayMessage("你好，我是延时主题消息","queueDelayT",5,TimeUnit.SECONDS);
//        // 设置优先级，消息会插入到队列头
//        producer.sendPriorityMessage("你好，我是队列消息","queue");
    }
}
