package com.xymq_cli.handler;

import com.alibaba.fastjson.JSON;
import com.xymq_cli.constant.Destination;
import com.xymq_cli.constant.MessageType;
import com.xymq_cli.listener.MessageData;
import com.xymq_cli.listener.MessageListener;
import com.xymq_common.message.Message;
import com.xymq_common.protocol.MessageUtils;
import com.xymq_common.protocol.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author 黎勇炫
 * @date 2022年07月11日 15:45
 */
public class ConsumerHandler extends SimpleChannelInboundHandler<Protocol> {

     /**
       * 要消费的队列满
       */
    private String destination;
     /**
       * 监听器
       */
    private MessageListener messageListener;
     /**
       * 自动签名(默认开启)
       */
    private boolean isAutoAcknowledge = true;

    /**
     * 日志信息
     */
    private Logger logger = LoggerFactory.getLogger(ConsumerHandler.class);

    public ConsumerHandler(String destination){
        this.destination = destination;
    }

    /**
     * 消息监听通道，加入有消息就会调用这个方法
     * @param ctx
     * @param protocol
     * @return void
     * @author 黎勇炫
     * @create 2022/7/11
     * @email 1677685900@qq.com
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol protocol) throws Exception {
        // 将字节数组转换成消息对象
        Message message = MessageUtils.reverse(protocol.getContent());
        if(message != null){
            execListener(new MessageData(this,message.getContent()));
            if(isAutoAcknowledge){
                ack(ctx.channel(), message.getMessageId(), message.getDestination());
            }
        }
    }

    /**
     * 向服务端签收指定的消息
     * @param channel
     * @param messageId
     * @param destination
     * @return void
     * @author 黎勇炫
     * @create 2022/7/11
     * @email 1677685900@qq.com
     */
    private void ack(Channel channel, Long messageId, String destination) {
        Message message = new Message(messageId, MessageType.ACK.getType(), null,destination, Destination.TOPIC.getDestination(),false,0,TimeUnit.MILLISECONDS);
        byte[] content = JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8);
        Protocol protocol = new Protocol(content.length,content);
        channel.writeAndFlush(protocol);
    }

    /**
     * 客户端一启动成功就向服务端注册这个通道，告诉服务端这是消费者
     * @param ctx
     * @return void
     * @author 黎勇炫
     * @create 2022/7/11
     * @email 1677685900@qq.com
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 注册
        register(ctx.channel());
    }

    /**
     * 向服务端注册消费者
     * @param channel
     * @return void
     * @author 黎勇炫
     * @create 2022/7/11
     * @email 1677685900@qq.com
     */
    private void register(Channel channel) {
        Message message = new Message(null, MessageType.COMSUMER.getType(), null, this.destination,  Destination.QUEUE.getDestination(), false,0, TimeUnit.MILLISECONDS);
        byte[] content = JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8);
        Protocol protocol = new Protocol(content.length,content);
        channel.writeAndFlush(protocol);
    }

     /**
       * 消息监听器
       */
    public void createListener(MessageListener messageListener){
        this.messageListener = messageListener;
    }

     /**
       * 执行监听事件
       */
    private void execListener(MessageData data)  {
        this.messageListener.getMessage(data);
    }

     /**
       * 设置自动签名
       */
    public void setAutoAcknowledge(boolean autoAcknowledge){
        this.isAutoAcknowledge = autoAcknowledge;
    }

     /**
       * 获取消息监听器
       */
    public MessageListener getMessageListener(){
        return this.messageListener;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("关闭与服务端的连接");
        ctx.channel().close();
    }
}
