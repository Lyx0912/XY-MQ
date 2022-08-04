package com.xymq_cli.core;

import com.xymq_cli.handler.XyTextWebSocketFrameHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author 黎勇炫
 * @date 2022年08月04日 15:59
 */
public class WebsocketInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        ChannelPipeline pipeline = sc.pipeline();
        // 使用http的编解码器
        pipeline.addLast(new HttpServerCodec())
                // 以块方式写，添加ChunkedWriteHandler处理器
                .addLast(new ChunkedWriteHandler())
                 /**
                   * http在传输过程中是以块传输的，HttpObjectAggregator可以将这些块聚合起来
                   * 这就是为什么http在发送大量请求时会发起多个请求
                   */
                .addLast(new HttpObjectAggregator(8192))
                // websocket在传输中是以帧(frame)的形式传输的。请求ws://127.0.0.1:8687/connect
                .addLast(new WebSocketServerProtocolHandler("/connect"))
                .addLast(new XyTextWebSocketFrameHandler());
    }
}
