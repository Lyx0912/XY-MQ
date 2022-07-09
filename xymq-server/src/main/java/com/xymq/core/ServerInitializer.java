package com.xymq.core;

import com.xymq.handler.MessageHandler;
import com.xymq.protocol.MessageDecoder;
import com.xymq.protocol.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 黎勇炫
 * @date 2022年07月09日 17:32
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        ChannelPipeline pipeline = sc.pipeline();
        pipeline.addLast("encoder",new MessageEncoder())
                .addLast("decoder",new MessageDecoder())
                .addLast(new MessageHandler());
    }
}
