package com.xymq_cli.web.service.impl;

import com.xymq_cli.core.XymqServer;
import com.xymq_cli.execution.AckExec;
import com.xymq_cli.execution.ProducerExec;
import com.xymq_cli.web.service.DataChartService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author 黎勇炫
 * @date 2022年08月03日 21:42
 */
@Service
public class DataChartServiceImpl implements DataChartService {

    private ProducerExec producerExec;
    private AckExec ackExec;
    private XymqServer xymqServer;
     /**
       * websocket客户端容器
       */
    private static List<Channel> channels = new ArrayList<>();
     /**
       * 计数器
       */
    private LongAdder counter = new LongAdder();
    /**
       * 时间数组
       */
    private List<String> timeList = new ArrayList<>();
     /**
       * 消费成功数量
       */
    private List<Long> successQueueCount = new ArrayList<>();
     /**
       * 队列消息堆积情况
       */
    private List<Long> accQueueCount = new ArrayList<>();
     /**
       * 总数量
       */
    private List<Long> queueTotal = new ArrayList<>();
     /**
       * 返回结果的map
       */
    private Map<String,Object> data = new HashMap<>();

    @Autowired
    public DataChartServiceImpl(ProducerExec producerExec, AckExec ackExec, XymqServer xymqServer) {
        this.producerExec = producerExec;
        this.ackExec = ackExec;
        this.xymqServer = xymqServer;
        initData();
    }

    /**
     * 初始化数据
     * @return void
     * @author 黎勇炫
     * @create 2022/8/4
     * @email 1677685900@qq.com
     */
    private void initData() {
        // 初始化数据组
        add();
        counter.increment();
    }

    /**
     * 删除索引位置0的数据，将最新的数据填充到末尾
     * @return void
     * @author 黎勇炫
     * @create 2022/8/4
     * @email 1677685900@qq.com
     */
    public void resetData(){
        // 清除数据
        removeIndex0();
        add();
    }

    /**
     * 往数据列表中更新数据
     * @return void
     * @author 黎勇炫
     * @create 2022/8/4
     * @email 1677685900@qq.com
     */
    private void add() {
        timeList.add(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
        successQueueCount.add(ackExec.getQueueMessageCount());
        queueTotal.add(producerExec.getQueuetTotalCount());
        accQueueCount.add(xymqServer.getUnReadQueueMessageCount());
    }

    /**
     * 删除索引位置0的数据
     * @return void
     * @author 黎勇炫
     * @create 2022/8/4
     * @email 1677685900@qq.com
     */
    private void removeIndex0() {
        timeList.remove(0);
        successQueueCount.remove(0);
        queueTotal.remove(0);
        accQueueCount.remove(0);
    }

    /**
     * 获取队列详细数据
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author 黎勇炫
     * @create 2022/8/3
     * @email 1677685900@qq.com
     */
    @Override
    public Map<String, Object> queueData() {
        data.put("time",timeList);
        data.put("successQueueCount",successQueueCount);
        data.put("accQueueCount",accQueueCount);
        data.put("queueTotal",queueTotal);
        return data;
    }

     /**
       * 添加客户端
       */
    public static void putChannel(Channel channel){
        channels.add(channel);
    }

    /**
     * 每隔5s获取一次
     * @return void
     * @author 黎勇炫
     * @create 2022/8/4
     * @email 1677685900@qq.com
     */
    @Override
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void updateData(){
        // 如果索引值已经
        if(counter.intValue() == 6){
            resetData();
        }else {
            add();
            counter.increment();
        }
        Iterator iterator = channels.iterator();
        while (iterator.hasNext()){
            Channel channel = (Channel) iterator.next();
            if(!channel.isActive()){
                iterator.remove();
                return;
            }
            channel.writeAndFlush(new TextWebSocketFrame(queueData().toString()));
        }
    }
}
