package com.xymq_cli.web.domain;

import lombok.Data;

/**
 * @author 黎勇炫
 * @date 2022年08月09日 18:53
 */
@Data
public class QueueVO {

    private String queueName;
    private String type;
    private Integer consumerCount;
    private String unConsume;
}
