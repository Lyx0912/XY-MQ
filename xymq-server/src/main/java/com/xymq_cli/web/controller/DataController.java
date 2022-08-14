package com.xymq_cli.web.controller;

import com.alibaba.fastjson.JSON;
import com.xymq_cli.web.service.DataChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 黎勇炫
 * @date 2022年08月01日 22:15
 */
@RestController
@RequestMapping("/xy/data")
public class DataController {

    @Autowired
    private DataChartService dataChartService;

    @GetMapping("/queue")
    public String queueCharts(){
        return JSON.toJSONString(dataChartService.queueData());
    }

    /**
     * 获取队列详情列表
     * @return java.lang.String
     * @author 黎勇炫
     * @create 2022/8/14
     * @email 1677685900@qq.com
     */
    @GetMapping("/list")
    public String queueDetail(){
        return JSON.toJSONString(dataChartService.queueDetail());
    }
}
