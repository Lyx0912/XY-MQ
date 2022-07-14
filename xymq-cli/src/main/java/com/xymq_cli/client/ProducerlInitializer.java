package com.xymq_cli.client;

import com.xymq_cli.handler.ProducerHandler;
import com.xymq_common.protocol.MessageDecoder;
import com.xymq_common.protocol.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author 黎勇炫
 * @date 2022年07月14日 20:24
 */
public class ProducerlInitializer extends ChannelInitializer<SocketChannel> {

    private ProducerHandler producerHandler;

    public ProducerlInitializer(ProducerHandler producerHandler) {
        this.producerHandler = producerHandler;
    }

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        ChannelPipeline pipeline = sc.pipeline();
        pipeline.addLast("encoder",new MessageEncoder())
                .addLast("decoder",new MessageDecoder())
                .addLast(this.producerHandler);

    }
}
