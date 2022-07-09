package com.xymq.handler;

import com.xymq.message.Message;
import com.xymq.protocol.Protocol;
import com.xymq.util.Message2Byte;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author 黎勇炫
 * @date 2022年07月09日 17:57
 */
public class MessageHandler extends SimpleChannelInboundHandler<Protocol> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Protocol protocol) throws Exception {
        // 将字节数组转换成消息对象
        Message res = Message2Byte.reverse(protocol.getContent());
        System.out.println(res);
        System.out.println("收到消息："+new String(protocol.getContent(), CharsetUtil.UTF_8));
    }

}
