package com.xymq_cli.web.service.impl;

import com.xymq_cli.web.service.DataChartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 黎勇炫
 * @date 2022年08月03日 21:42
 */
@Service
public class DataChartServiceImpl implements DataChartService {

    private List<String> timeList;
    private Map<String,Object> data = new HashMap<>();

    {
        // 初始化时间数组
        timeList = new ArrayList<>();
        timeList.add(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")));
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
        return data;
    }
}
