package com.xymq_cli.web.service;

import java.util.Map;

/**
 * 数据图表业务层
 * @author 黎勇炫
 * @date 2022年08月03日 21:40
 */
public interface DataChartService {

    /**
     * 获取队列详细数据
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author 黎勇炫
     * @create 2022/8/3
     * @email 1677685900@qq.com
     */
    public Map<String,Object> queueData();
}
