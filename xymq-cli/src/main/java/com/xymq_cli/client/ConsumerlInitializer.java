package com.xymq_cli.client;

import com.xymq_cli.handler.ConsumerHandler;
import com.xymq_cli.protocol.MessageDecoder;
import com.xymq_cli.protocol.MessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 消费者初始化器
 * @author 黎勇炫
 * @date 2022年07月11日 15:40
 */
public class ConsumerlInitializer extends ChannelInitializer<SocketChannel> {

    private String destination;
    private ConsumerHandler consumerHandler;

    public ConsumerlInitializer(ConsumerHandler consumerHandler){
        this.consumerHandler = consumerHandler;
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
        pipeline.addLast("encoder",new MessageEncoder())
                .addLast("decoder",new MessageDecoder())
                .addLast(consumerHandler);
    }
}
