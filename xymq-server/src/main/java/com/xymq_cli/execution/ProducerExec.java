package com.xymq_cli.execution;

import com.xymq_cli.constant.Destination;
import com.xymq_cli.constant.MessageConstant;
import com.xymq_cli.core.StorageHelper;
import com.xymq_cli.core.XymqServer;
import com.xymq_cli.message.Message;
import com.xymq_cli.util.SnowflakeIdUtils;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生产者消息处理器
 * @author 黎勇炫
 * @date 2022年07月10日 12:39
 */
@Component
public class ProducerExec implements Execution{

    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    @Autowired
    private XymqServer xymqServer;
    @Autowired
    private StorageHelper storageHelper;

    /**
     * 处理来自生产者推送的消息，根据消息对象的不同情况将消息存储到不同的消息容器中
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    @Override
    public void exec(Message message, Channel channel) {
        // 先判断是队列消息还是主题消息，然后放入消息容器
        message.setMessageId(snowflakeIdUtils.nextId());
        if (message.getDestinationType() == Destination.QUEUE.getDestination()) {
            execQueueMessage(message);
        } else {
            execTopicMessage(message);
        }
    }

    /**
     * 处理队列消息
     * @param message
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    private void execQueueMessage(Message message) {
        if (message.getDelay() == 0) {
            storageHelper.storeQueueMessage(xymqServer.getQueueContainer(), message);
        } else {
            storageHelper.storeDelayMessage(xymqServer.getDelayQueueContainer(), message);
        }
    }

    /**
     * 处理主题消息
     * @param message 消息对象
     * @return void
     * @author 黎勇炫
     * @create 2022/7/10
     * @email 1677685900@qq.com
     */
    private void execTopicMessage(Message message) {
        if (xymqServer.getTopicContainer().containsKey(message.getDestination())) {
            if(message.getDelay() == 0){
                storageHelper.storeTopicMessage(xymqServer.getTopicContainer(), message);
            }else {
                storageHelper.storeDelayTopicMessage(xymqServer.getDelayTopicContainer(), message);
            }
        }
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
        return MessageConstant.PROVIDER;
    }
}
