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
        NioEventLoopGroup clientGroup = new NioEventLoopGroup();

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
    public void sendMsg(String content, String destinationName) {
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
     *
     * @param content         消息内容
     * @param destinationName i
     * @return void
     * @author 黎勇炫
     * @create 2022/7/13
     * @email 1677685900@qq.com
     */
    public void publish(String content, String destinationName) {
//        MessageBean messageBean = new MessageBean(null, MessageType.PRIVODER.getType(), content,destinationName,DestinationType.TOPIC.getType(),false,0,null);
//        String message = JSON.toJSONString(messageBean);
//        ByteBuffer buffer = ByteBuffer.allocate(4+ ByteBufferUtils.getByteSize(message));
//        buffer.putInt(ByteBufferUtils.getByteSize(message));
//        buffer.put(message.getBytes());
//        buffer.flip();
//        try {
//            socketChannel.write(buffer);
//        } catch (IOException e) {
//            logger.error("Write buffer failed");
//        }
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
    public void publishDelayMessage(String content, String destinationName, long delay, TimeUnit timeUnit) {
//        MessageBean messageBean = new MessageBean(null, MessageType.PRIVODER.getType(), content,destinationName,DestinationType.TOPIC.getType(),false,delay,timeUnit);
//        String message = JSON.toJSONString(messageBean);
//        ByteBuffer buffer = ByteBuffer.allocate(4+ ByteBufferUtils.getByteSize(message));
//        buffer.putInt(ByteBufferUtils.getByteSize(message));
//        buffer.put(message.getBytes());
//        buffer.flip();
//        try {
//            socketChannel.write(buffer);
//        } catch (IOException e) {
//            logger.error("Write buffer failed");
//        }
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
//        MessageBean messageBean = new MessageBean(null, MessageType.PRIVODER.getType(), content,destinationName, DestinationType.QUEUE.getType(),true,0,TimeUnit.MILLISECONDS);
//        String message = JSON.toJSONString(messageBean);
//        ByteBuffer buffer = ByteBuffer.allocate(4+ ByteBufferUtils.getByteSize(message));
//        buffer.putInt(ByteBufferUtils.getByteSize(message));
//        buffer.put(message.getBytes());
//        buffer.flip();
//        try {
//            socketChannel.socket().setSendBufferSize(1024*10000);
//        } catch (SocketException e) {
//            logger.error("producer failed to connect to the server ");
//        }
//        try {
//            socketChannel.write(buffer);
//        } catch (IOException e) {
//            logger.error("Write buffer failed");
//        }
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
        // 关闭工作组
        this.channel.close();
    }


    public static void main(String[] args) {
        Producer producer = new Producer();
        for (int i = 0; i < 10; i++) {
            producer.sendDelayMessage("你好", "queue",5,TimeUnit.SECONDS);
        }
    }
}
