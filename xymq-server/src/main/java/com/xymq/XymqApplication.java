package com.xymq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/*
 * @author 黎勇炫
 * @create 2022/7/9
 * @email 1677685900@qq.com
 */
@SpringBootApplication
@EnableScheduling // 定时任务
@EnableAsync // 异步任务
public class XymqApplication {

    public static void main(String[] args) {
        SpringApplication.run(XymqApplication.class, args);
    }

}
