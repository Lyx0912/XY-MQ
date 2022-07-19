package com.xymq_cli.core;

import com.xymq_common.message.Message;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理客户端-及时清理下线的客户端
 * @author 黎勇炫
 * @date 2022年07月19日 15:22
 */
@Component
public class ClientManager {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private LevelDb levelDb;

     /**
       * 存储离线的消费者和未消费的信息数据
       */
    public void storeOfflineData(ConcurrentHashMap<Long, ArrayList<Message>> offLineTopicMessage,ConcurrentHashMap<String, HashMap<Long, Channel>> offLineSubscriber){
        CompletableFuture.runAsync(()->{
            if (CollectionUtils.isEmpty(offLineSubscriber)) {
                levelDb.storeOffLineMessage(offLineTopicMessage);
            }
            if (null != offLineSubscriber) {
                ConcurrentHashMap<String, HashMap<Long, Channel>> cpOffLineSubscriber = new ConcurrentHashMap<>();
                cpOffLineSubscriber.putAll(offLineSubscriber);
//            for (ConcurrentHashMap.Entry<String, HashMap<Long, Channel>> stringHashMapEntry : cpOffLineSubscriber.entrySet()) {
//                for (ConcurrentHashMap.Entry<Long, Channel> longSocketChannelEntry : stringHashMapEntry.getValue().entrySet()) {
//                    longSocketChannelEntry.setValue(SocketChannel.open());
//                }
//            }
                levelDb.storeOffLineSubscriber(cpOffLineSubscriber);
            }
        },taskExecutor);
    }

     /**
       * 向消费这发送心跳包，如果消费者离线，就把消费者从消费队列中剔除，放入离线队列中
       */
    public void clean(ConcurrentHashMap<String, List<Channel>> consumerContainer) {
        CompletableFuture.runAsync(()->{
            for (Map.Entry<String, List<Channel>> entry : consumerContainer.entrySet()) {
                List<Channel> list = entry.getValue();
                Iterator iterator = list.iterator();
                while (iterator.hasNext()) {
                    Channel client = (Channel) iterator.next();
                    if (!client.isActive()) {
                        iterator.remove();
                    }
                }
            }
        },taskExecutor);
    }
}
