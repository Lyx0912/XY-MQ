package com.xymq_cli.execution;

import com.xymq_cli.constant.MessageConstant;
import com.xymq_cli.core.LevelDb;
import com.xymq_common.message.Message;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 黎勇炫
 * @date 2022年07月10日 12:42
 */
@Component
public class AckExec implements Execution{

    @Autowired
    private LevelDb levelDb;

    /**
     * 执行操作(消费、推送和签收)
     * @param message
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    @Override
    public void exec(Message message, Channel channel) {
        // 直接操作数据库删除消息
        levelDb.deleteMessageBean(message.getMessageId());
    }

    /**
     * 返回当前策略支持的 消息 类型
     *
     * @return int 消息类型
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    @Override
    public int getType() {
        return MessageConstant.ACK;
    }
}
