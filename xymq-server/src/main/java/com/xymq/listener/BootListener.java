package com.xymq.listener;

import com.xymq.core.XymqServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 监听springboot启动的生命周期，当springboot完成所有bean的实例化和初始化后就启动netty服务
 * @author 黎勇炫
 * @date 2022年07月09日 18:08
 */
@Component
public class BootListener implements ApplicationListener<ApplicationEvent> {

    @Autowired
    private XymqServer xymqServer;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if(event instanceof ApplicationReadyEvent){
            xymqServer.init();
        }
    }
}
