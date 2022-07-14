package com.xymq_cli.handler;

import com.xymq_common.protocol.Protocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author 黎勇炫
 * @date 2022年07月14日 20:26
 */
public class ProducerHandler extends SimpleChannelInboundHandler<Protocol> {

     /**
       * 客户端连接成功就向服务端注册自己
       */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        register();
    }

     /**
       * 
       */
    private void register() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Protocol protocol) throws Exception {

    }
}