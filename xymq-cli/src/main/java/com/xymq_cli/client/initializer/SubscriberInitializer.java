package com.xymq_cli.client.initializer;

import com.xymq_cli.handler.SubscriberHandler;
import com.xymq_common.protocol.MessageDecoder;
import com.xymq_common.protocol.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 黎勇炫
 * @date 2022年07月17日 16:56
 */
public class SubscriberInitializer extends ChannelInitializer<SocketChannel> {

     /**
       * 订阅者消息处理器
       */
    private SubscriberHandler subscriberHandler;

    public SubscriberInitializer(SubscriberHandler subscriberHandler) {
        this.subscriberHandler = subscriberHandler;
    }

    /**
     * 该方法主要是为客户端channel设置编解码器以及消息处理器
     * @param sc netty通道
     * @return void
     * @author 黎勇炫
     * @create 2022/7/11
     * @email 1677685900@qq.com
     */
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        ChannelPipeline pipeline = sc.pipeline();
        pipeline.addLast("encoder",new MessageEncoder());
        pipeline.addLast("decoder",new MessageDecoder());
        pipeline.addLast(subscriberHandler);
    }
}
