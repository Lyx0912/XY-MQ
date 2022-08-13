package com.xymq_cli.web.domain;

import lombok.Data;

/**
 * @author 黎勇炫
 * @date 2022年08月09日 18:53
 */
@Data
public class QueueVO {

     /**
       * 队列名称
       */
    private String queueName;
     /**
       * 客户端数量
       */
    private Integer consumerCount = 0;
     /**
       * 消息堆积数量
       */
    private Long unConsume = 0L;
     /**
       * 延时消息数量
       */
    private Long delayCount = 0L;
}
