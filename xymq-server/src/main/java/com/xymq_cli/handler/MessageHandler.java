package com.xymq_cli.handler;

import com.xymq_cli.message.Message;
import com.xymq_cli.protocol.Protocol;
import com.xymq_cli.execution.Execution;
import com.xymq_cli.execution.ExecutionFactory;
import com.xymq_cli.util.Message2Byte;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 黎勇炫
 * @date 2022年07月09日 17:57
 */
public class MessageHandler extends SimpleChannelInboundHandler<Protocol> {
     /**
       * 日志
       */
    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol protocol) throws Exception {
        // 将字节数组转换成消息对象
        Message msg = Message2Byte.reverse(protocol.getContent());
        System.out.println(msg);
        // 判断是消费者还是消息推送者，如果是消费者就将channel保存到容器，如果是推送者就将消息存储到对应的队列中,如果是签收消息就将消息在leveldb中移除
        Execution execution = ExecutionFactory.getStrategy(msg.getType());
        execution.exec(msg,ctx.channel());
    }

    /**
     * 出现异常就关闭客户端连接
     * @param ctx channelhandler上下文
     * @param cause
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端{}断开连接",ctx.channel().remoteAddress());
        ctx.channel().close();
    }
}
