package com.xymq_cli.handler;

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

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 订阅者的消息处理器
 * @author 黎勇炫
 * @date 2022年07月17日 16:59
 */
public class SubscriberHandler extends SimpleChannelInboundHandler<Protocol> {

     /**
       * 订阅的主题
       */
    private String destination;
     /**
       * 订阅者唯一标识
       */
    private long subscriberId;
    /**
     * 监听器
     */
    private MessageListener messageListener;

    /**
     * 日志信息
     */
    private Logger logger = LoggerFactory.getLogger(SubscriberHandler.class);

    public SubscriberHandler(String destination, long subscriberId) {
        this.destination = destination;
        this.subscriberId = subscriberId;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol protocol) throws Exception {
        // 将字节数组转换成消息对象
        Message message = MessageUtils.reverse(protocol.getContent());
        if(message != null){
            execListener(new MessageData(this,message.getContent()));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 向服务端注册该订阅者
        retister(ctx.channel());
    }

    /**
     * 构建一个消息对象，向服务端注册自己
     * @return void
     * @author 黎勇炫
     * @create 2022/7/17
     * @email 1677685900@qq.com
     */
    private void retister(Channel channel) {
        Message message = new Message(subscriberId, MessageType.COMSUMER.getType(), null, this.destination,  Destination.TOPIC.getDestination(), false,0, TimeUnit.MILLISECONDS);
        channel.writeAndFlush(MessageUtils.message2Protocol(message));
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
}
