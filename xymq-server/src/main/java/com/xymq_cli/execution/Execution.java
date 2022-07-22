package com.xymq_cli.execution;

import com.xymq_common.message.Message;
import io.netty.channel.Channel;

/**
 * @author 黎勇炫
 * @date 2022年07月10日 12:35
 */
public interface Execution {
    /**
     * 执行操作(消费、推送和签收)
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    void exec(Message message, Channel channel);

    /**
     * 返回当前策略支持的 消息 类型
     * @return int 消息类型
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    int getType();
}