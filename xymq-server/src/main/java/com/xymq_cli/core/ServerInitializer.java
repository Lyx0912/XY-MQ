package com.xymq_cli.core;

import com.xymq_cli.handler.MessageHandler;
import com.xymq_cli.protocol.MessageDecoder;
import com.xymq_cli.protocol.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 为netty的channel通道添加编解码器和消息处理器
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
