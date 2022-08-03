package com.xymq_cli.web.controller;

import com.xymq_cli.web.service.DataChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public Map<String,Object> queueCharts(){
        return dataChartService.queueData();
    }
}
